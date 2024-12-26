package com.example.musicapp.api

import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.profile.data.User
import com.example.musicapp.features.main.profile.data.Playlist
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Body

import com.google.gson.annotations.SerializedName

data class SongsResponse(
    @SerializedName("songs") val songs: List<Track>
)

data class Track(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("fileUrl") val fileUrl: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("duration") val duration: String
)


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

data class FavouriteRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("song_id") val songId: String
)

data class FavouritesResponse(
    @SerializedName("songs") val songs: List<Track>
)

data class SearchResponse(
    @SerializedName("songs") val songs: List<Track>
)


interface ApiService {
    @GET("posts/1") // Запит до одного поста з JSONPlaceholder
    suspend fun getPost(): Post

    @GET("user/profile")
    suspend fun getUserProfile(): UserProfileResponse

    @GET("songs")
    suspend fun getTracks(): SongsResponse

    @GET("songs/{trackId}")
    suspend fun getTrackById(@Path("trackId") trackId: String): Track

    @GET("songs/search/{query}")
    suspend fun searchTracks(@Path("query") query: String): SearchResponse


    @GET("songs/favourites/{user_id}")
    suspend fun getFavourites(@Path("user_id") userId: String): FavouritesResponse

    @POST("songs/favourites")
    suspend fun addFavourite(@Body favourite: FavouriteRequest)

    @DELETE("songs/favourites/{user_id}/{song_id}")
    suspend fun deleteFavourite(
        @Path("user_id") userId: String,
        @Path("song_id") songId: String
    )
}





