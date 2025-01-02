package com.example.musicapp.features.main.artists.data

import android.util.Log
import com.example.musicapp.api.Album
import com.example.musicapp.api.AlbumResponse
import com.example.musicapp.api.ApiService
import com.example.musicapp.api.ArtistResponse
import com.example.musicapp.features.main.likedtracks.data.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val _artistState = MutableStateFlow<ArtistResult>(ArtistResult.Loading)
    val artistState = _artistState.asStateFlow()

    suspend fun getArtistDetails(artistId: String): ArtistResponse {
        return try {
            val response = apiService.getArtistById(artistId.toInt())
            _artistState.value = ArtistResult.Success(response)
            response
        } catch (e: Exception) {
            Log.e("ArtistRepository", "Error fetching artist details: ${e.message}")
            _artistState.value = ArtistResult.Error("Error fetching artist details: ${e.message}")
            throw e
        }
    }

    suspend fun getArtistAlbums(artistId: String): List<Album> {
        return try {
            val response = apiService.getAlbumsByArtist(artistId.toInt())
            response.albums
        } catch (e: Exception) {
            Log.e("ArtistRepository", "Error fetching albums: ${e.message}")
            emptyList()
        }
    }

    suspend fun getArtistTracks(artistId: String): List<Track> {
        return try {
            val response = apiService.getSongsByArtist(artistId.toInt())
            response.songs
        } catch (e: Exception) {
            Log.e("ArtistRepository", "Error fetching tracks: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAlbumDetails(albumId: String): AlbumResponse {
        return try {
            val response = apiService.getAlbumById(albumId.toInt())
            response
        } catch (e: Exception) {
            Log.e("ArtistRepository", "Error fetching album details: ${e.message}")
            throw e
        }
    }

    suspend fun getAlbumTracks(albumId: String): List<Track> {
        return try {
            val response = apiService.getSongsByAlbum(albumId.toInt())
            response.songs
        } catch (e: Exception) {
            Log.e("ArtistRepository", "Error fetching album tracks: ${e.message}")
            emptyList()
        }
    }
}

sealed class ArtistResult {
    object Loading : ArtistResult()
    data class Success(val artist: ArtistResponse) : ArtistResult()
    data class Error(val message: String) : ArtistResult()
}


