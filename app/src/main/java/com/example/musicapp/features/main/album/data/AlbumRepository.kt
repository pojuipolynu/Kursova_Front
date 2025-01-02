package com.example.musicapp.features.main.album.data

import android.util.Log
import com.example.musicapp.api.Album
import com.example.musicapp.api.ApiService
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val apiService: ApiService
) {

    // Отримати всіх виконавців через API
    suspend fun getAlbums(): List<Album> {
        return try {
            val response = apiService.getAlbums()
            response.albums // Отримуємо список співаків із JSON
        } catch (e: Exception) {
            Log.e("AlbumRepository", "Error fetching tracks: ${e.message}")
            emptyList()
        }
    }


    // Отримати виконавців по ID
    suspend fun getAlbumById(albumId: String): Album? {
        return try {
            apiService.getAlbumById(albumId) // Метод API для отримання виконавців по ID
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun searchAlbums(query: String): List<Album> {
        return try {
            val response = apiService.searchAlbums(query)
            response.albums // Повертаємо список виконавців
        } catch (e: Exception) {
            Log.e("ArtistRepository", "Error searching for track: ${e.message}")
            emptyList()
        }
    }
}


//data class Album(
//    val id: String,
////    val imageUrl: String,
//    val title: String,
//    val artist_id: Int
//)
