package com.example.musicapp.features.main.profile.data

import android.content.Context
import android.util.Log
import com.example.musicapp.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val _profileState = MutableStateFlow<ProfileResult>(ProfileResult.Loading)
    val profileState = _profileState.asStateFlow()

    suspend fun getUserStatus(userId: String): String {
        return try {
            val response = apiService.getUserStatus(userId)
            Log.d("ProfileRepository", "Status: ${response.status}")
            _profileState.value = ProfileResult.Success(response.status)
            response.status
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error getting status: ${e.message}")
            _profileState.value = ProfileResult.Error("Error getting status: ${e.message}")
            "FREE"
        }
    }

    suspend fun changeUserStatus(userId: String) {
        try {
            apiService.changeUserStatus(userId)
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error changing status: ${e.message}")
        }
    }

    suspend fun getUserPlaylists(userId: String): List<Playlist> {
        return try {
            val response = apiService.getUserPlaylists(userId)
            response
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error fetching playlists: ${e.message}")
            emptyList()
        }
    }

}


sealed class ProfileResult {
    data object Loading : ProfileResult()
    data class Success(val userStatus: String) : ProfileResult()
    data class Error(val message: String) : ProfileResult()
}