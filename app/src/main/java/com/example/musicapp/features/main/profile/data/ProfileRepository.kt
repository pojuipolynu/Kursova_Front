package com.example.musicapp.features.main.profile.data

import android.content.Context
import com.example.musicapp.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: MockApiService
//    private val apiService: ApiService
) {
    private val _profileState = MutableStateFlow<ProfileResult>(ProfileResult.Loading)
    val profileState = _profileState.asStateFlow()

    suspend fun fetchUserProfile() {
        try {
            val response = apiService.getUserProfile()
            val userProfile = UserProfile(
                user = response.user,
                playlists = response.playlists
            )
            _profileState.value = ProfileResult.Success(userProfile)
        } catch (e: Exception) {
            _profileState.value = ProfileResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}

sealed class ProfileResult {
    data object Loading : ProfileResult()
    data class Success(val userProfile: UserProfile) : ProfileResult()
    data class Error(val message: String) : ProfileResult()
}