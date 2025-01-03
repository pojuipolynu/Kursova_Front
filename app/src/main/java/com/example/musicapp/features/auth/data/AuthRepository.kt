package com.example.musicapp.features.auth.data

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.musicapp.R
import com.example.musicapp.api.ApiService
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authPreferencesManager: AuthPreferencesManager,
    private val context: Context,
    private val apiService: ApiService
) {
    private val _authResult = MutableStateFlow<AuthResult>(
        if (authPreferencesManager.isUserLoggedIn())
            AuthResult.Success(firebaseAuth.currentUser)
        else
            AuthResult.Idle
    )
    val authResult = _authResult.asStateFlow()

    suspend fun signInWithGoogle(activity: Activity) {
        _authResult.value = AuthResult.Loading

        try {
            // Create Google ID Option
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.your_web_client_id))
                .build()

            // Create Credential Request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // Get Credential from Credential Manager
            val credentialManager = CredentialManager.create(activity)
            val result: GetCredentialResponse = credentialManager.getCredential(
                activity,
                request
            )

            // Extract Google ID Token Credential
            val credential = GoogleIdTokenCredential.createFrom(result.credential.data)

            // Firebase Authentication
            val firebaseCredential = GoogleAuthProvider.getCredential(
                credential.idToken,
                null
            )

            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()

            // Save user ID to SharedPreferences
            authResult.user?.uid?.let { userId ->
                authPreferencesManager.saveUserId(userId)
            }

            _authResult.value = AuthResult.Success(authResult.user)
        } catch (e: Exception) {
            println("Error: $e")
            _authResult.value = AuthResult.Error(e.localizedMessage)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        authPreferencesManager.clearUserId()
        _authResult.value = AuthResult.Idle
    }

    fun getCurrentUser(): UserData? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let {
            UserData(
                userId = it.uid,
                username = it.displayName,
                email = it.email,
            )
        }
    }

    fun getCurrentUserId(): String? {
        Log.d("userID","Current user id: ${firebaseAuth.currentUser?.uid}")
        return firebaseAuth.currentUser?.uid ?: "1"
    }

    suspend fun setUserStatus() {
        try {
            Log.d("setUserStatus", "Setting status for userId: ${firebaseAuth.currentUser?.uid}")
            apiService.setUserStatus(firebaseAuth.currentUser?.uid ?: " ")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error setting user status: ${e.message}")
        }
    }

    fun isUserLoggedIn(): Boolean = authPreferencesManager.isUserLoggedIn()
}