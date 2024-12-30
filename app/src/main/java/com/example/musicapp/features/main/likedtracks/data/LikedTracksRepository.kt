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
            val response = apiService.getFavourites(userId)
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