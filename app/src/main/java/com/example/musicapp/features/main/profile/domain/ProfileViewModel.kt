package com.example.musicapp.features.main.profile.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.features.main.profile.data.ProfileRepository
import com.example.musicapp.features.main.profile.data.ProfileResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileRepository: ProfileRepository) : ViewModel() {
    val profileState: StateFlow<ProfileResult> = profileRepository.profileState

    fun loadUserProfile() {
        viewModelScope.launch {
            profileRepository.fetchUserProfile()
        }
    }
}