package com.example.musicapp.features.main.profile.data

import com.example.musicapp.api.UserProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class User(val id: String, val name: String, val email: String)
data class Playlist(val id: String, val title: String, val tracks: List<String>)

data class UserProfile(val user: User, val playlists: List<Playlist>)

// Mock API Service

class MockApiService @Inject constructor() {
    suspend fun getUserProfile(): UserProfileResponse {
        return UserProfileResponse(
            user = User(id = "1", name = "John Doe", email = "john.doe@example.com"),
            playlists = listOf(
                Playlist(id = "1", title = "Favorites", tracks = listOf("Song 1", "Song 2")),
                Playlist(id = "2", title = "Workout Mix", tracks = listOf("Track A", "Track B"))
            )
        )
    }
}