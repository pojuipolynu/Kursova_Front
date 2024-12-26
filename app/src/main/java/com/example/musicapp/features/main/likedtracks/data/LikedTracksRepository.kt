package com.example.musicapp.features.main.likedtracks.data

import javax.inject.Inject
import javax.inject.Singleton
import com.example.musicapp.api.ApiService
import android.util.Log
import com.example.musicapp.api.FavouriteRequest
import com.example.musicapp.api.FavouritesResponse


@Singleton
class LikedTracksRepository @Inject constructor(
    private val apiService: ApiService
) {
//    private val tracks = listOf(
//        Track("1", "https://f005.backblazeb2.com/file/music-app-files/covers/SheWantsRevenge.st.jpg", "Sister", "She wants revenge", "4:19", "https://f005.backblazeb2.com/file/music-app-files/music/She+Wants+Revenge-Sister.mp3"),
//        Track("2", "https://f005.backblazeb2.com/file/music-app-files/covers/artworks-bQyGQjPCgQ4FIIAo-WbjuKg-t500x500.jpeg", "Billie Eilish", "My strange addiction", "2:59", "https://f005.backblazeb2.com/file/music-app-files/music/billie-eilish_-_my-strange-addiction.mp3"),
//        Track("3", "https://f005.backblazeb2.com/file/music-app-files/covers/500x500.jpg", "Your love", "She wants revenge", "3:36", "https://f005.backblazeb2.com/file/music-app-files/music/She+Wants+Revenge-Your+Love.mp3"),
//        Track("4", "https://f005.backblazeb2.com/file/music-app-files/covers/1_-JYPeNvOdIO52Z9TuV0Wzg.jpg", "The Hills", "The Weekend", "4:02", "https://f005.backblazeb2.com/file/music-app-files/music/The+Weeknd-The+Hills.mp3"),
//        Track("5", "https://f005.backblazeb2.com/file/music-app-files/covers/1_-JYPeNvOdIO52Z9TuV0Wzg.jpg", "The Hills", "The Weekend", "4:02", "https://f005.backblazeb2.com/file/music-app-files/music/The+Weeknd-The+Hills.mp3")
//    )
    private val likedTrackIds = mutableSetOf<String>()

    // Отримати всі треки через API
    suspend fun getTracks(): List<Track> {
        return try {
            val response = apiService.getTracks()
            response.songs // Отримуємо список треків із JSON
        } catch (e: Exception) {
            Log.e("LikedTracksRepository", "Error fetching tracks: ${e.message}")
            emptyList()
        }
    }


    // Отримати трек по ID
    suspend fun getTrackById(trackId: String): Track? {
        return try {
            apiService.getTrackById(trackId) // Метод API для отримання треку по ID
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

//    suspend fun toggleLike(trackId: String) {
//        if (likedTrackIds.contains(trackId)) {
//            likedTrackIds.remove(trackId)
//        } else {
//            likedTrackIds.add(trackId)
//        }
//    }

    suspend fun searchTracks(query: String): List<Track> {
        return try {
            val response = apiService.searchTracks(query)
            response.songs // Повертаємо список треків
        } catch (e: Exception) {
            Log.e("LikedTracksRepository", "Error searching for track: ${e.message}")
            emptyList()
        }
    }



    suspend fun getFavourites(userId: String): List<Track> {
        return try {
            val userIdInt = userId.toIntOrNull() ?: throw IllegalArgumentException("Invalid userId: $userId")
            val response = apiService.getFavourites(userIdInt.toString())
            response.songs // Витягніть список треків із об'єкта FavouritesResponse
        } catch (e: Exception) {
            Log.e("LikedTracksRepository", "Error fetching favourites: ${e.message}")
            emptyList()
        }
    }

    suspend fun getLikedTrackIds(userId: String): Set<String> {
        return try {
            val favourites = getFavourites(userId) // Отримуємо список улюблених треків
            favourites.map { it.id.toString() }.toSet() // Перетворюємо список треків у Set<String>
        } catch (e: Exception) {
            Log.e("LikedTracksRepository", "Error fetching liked track IDs: ${e.message}")
            emptySet()
        }
    }

    suspend fun addFavourite(userId: String, songId: String) {
        try {
            Log.d("addFavourite", "Adding favourite for userId: $userId, songId: $songId")
            apiService.addFavourite(FavouriteRequest(userId, songId))
        } catch (e: Exception) {
            Log.e("LikedTracksRepository", "Error adding favourite: ${e.message}")
        }
    }


    suspend fun deleteFavourite(userId: String, songId: String) {
        try {
            Log.d("deleteFavourite", "Deleting favourite for userId: $userId, songId: $songId")
            apiService.deleteFavourite(userId, songId)
        } catch (e: Exception) {
            Log.e("LikedTracksRepository", "Error deleting favourite: ${e.message}")
        }
    }



    suspend fun isTrackLiked(userId: String, trackId: String): Boolean {
        return try {
            val favourites = getFavourites(userId) // Отримуємо всі улюблені треки
            favourites.any { it.id.toString() == trackId }
        } catch (e: Exception) {
            Log.e("LikedTracksRepository", "Error checking if track is liked: ${e.message}")
            false
        }
    }



}

data class Track(
    val id: String,
    val imageUrl: String,
    val title: String,
    val artist: String,
    val duration: String,
    val fileUrl: String
)