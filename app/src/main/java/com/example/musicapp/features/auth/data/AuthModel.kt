package com.example.musicapp.features.auth.data
import com.google.firebase.auth.FirebaseUser

sealed class AuthResult {
    data object Idle : AuthResult()
    data object Loading : AuthResult()
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val message: String?) : AuthResult()
}

data class UserData(
    val userId: String? = null,
    val username: String? = null,
    val profilePictureUrl: String? = null
)