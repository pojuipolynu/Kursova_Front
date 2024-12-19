package com.example.musicapp.api

import com.example.musicapp.features.main.profile.data.User
import com.example.musicapp.features.main.profile.data.Playlist
import retrofit2.http.GET

data class UserProfileResponse(
    val user: User,
    val playlists: List<Playlist>
)

data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)


interface ApiService {
    @GET("posts/1") // Запит до одного поста з JSONPlaceholder
    suspend fun getPost(): Post

    @GET("user/profile")
    suspend fun getUserProfile(): UserProfileResponse
}




