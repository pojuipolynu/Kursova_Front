package com.example.musicapp.features.auth.domain

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.features.auth.data.AuthRepository
import com.example.musicapp.features.auth.data.AuthResult
import com.example.musicapp.features.auth.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authResult: StateFlow<AuthResult> = authRepository.authResult

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            authRepository.signInWithGoogle(activity)
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun getCurrentUser(): UserData? = authRepository.getCurrentUser()

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()
}