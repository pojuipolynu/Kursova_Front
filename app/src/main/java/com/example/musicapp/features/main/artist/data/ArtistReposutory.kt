package com.example.musicapp.features.main.artist.data

import javax.inject.Inject
import javax.inject.Singleton
import com.example.musicapp.api.ApiService
import android.util.Log
import com.example.musicapp.api.FavouriteRequest
import com.example.musicapp.api.FavouritesResponse
import com.example.musicapp.features.main.likedtracks.data.Track


@Singleton
class ArtistRepository @Inject constructor(
    private val apiService: ApiService
) {

    // Отримати всіх виконавців через API
    suspend fun getArtists(): List<Artist> {
        return try {
            val response = apiService.getArtists()
            response.artists // Отримуємо список співаків із JSON
        } catch (e: Exception) {
            Log.e("ArtistRepository", "Error fetching tracks: ${e.message}")
            emptyList()
        }
    }


    // Отримати виконавців по ID
    suspend fun getArtistById(artistId: String): Artist? {
        return try {
            apiService.getArtistById(artistId) // Метод API для отримання виконавців по ID
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun searchArtists(query: String): List<Artist> {
        return try {
            val response = apiService.searchArtists(query)
            response.artists // Повертаємо список виконавців
        } catch (e: Exception) {
            Log.e("ArtistRepository", "Error searching for track: ${e.message}")
            emptyList()
        }
    }

}

data class Artist(
    val id: String,
    val imageUrl: String,
    val name: String,
)