package com.example.musicapp.features.main.profile.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.features.main.profile.data.Playlist
import com.example.musicapp.features.main.profile.data.ProfileRepository
import com.example.musicapp.features.main.profile.data.ProfileResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel() {
    val profileState: StateFlow<ProfileResult> = profileRepository.profileState

    suspend fun getUserStatus(userId: String): String {
        return profileRepository.getUserStatus(userId)
    }

    fun changeUserStatus(userId: String) {
        viewModelScope.launch {
            try {
                profileRepository.changeUserStatus(userId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    val playlists = MutableStateFlow<List<Playlist>>(emptyList())

    fun fetchUserPlaylists(userId: String) {
        viewModelScope.launch {
            try {
                playlists.value = profileRepository.getUserPlaylists(userId)
            } catch (e: Exception) {
                playlists.value = emptyList()
            }
        }
    }
}