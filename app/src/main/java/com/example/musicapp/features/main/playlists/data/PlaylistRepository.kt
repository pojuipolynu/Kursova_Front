package com.example.musicapp.features.main.playlists.data

import android.util.Log
import com.example.musicapp.api.ApiService
import com.example.musicapp.api.PlaylistResponse
import com.example.musicapp.features.main.likedtracks.data.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val _allTracks = MutableStateFlow<List<Track>>(emptyList())
    val allTracks: Flow<List<Track>> = _allTracks.asStateFlow()

    suspend fun getPlaylistName(userId: String, playlistId: String): String {
        return try {
            Log.d("PlaylistRepository", "User ID: $userId, Playlist ID: $playlistId")
            val name = apiService.getPlaylistDetails(userId, playlistId.toInt()).title
            name ?: "Unknown Playlist"
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Error loading playlist name $e")
            "Error loading playlist name $e"
        }
    }


    fun getPlaylistTracksFlow(playlistId: String): Flow<List<Track>> {
        return flow {
            val response = apiService.getPlaylistTracks(playlistId)
            emit(response.tracks)
        }
    }

    suspend fun createPlaylist(userId: String, name: String): PlaylistResponse? {
        return try {
            Log.d("PlaylistRepository", "Creating playlist for user: $userId with name: $name")
            val request = CreatePlaylistRequest(title = name) // Формуємо тіло запиту
            apiService.createPlaylist(userId, request)
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Error creating playlist: ${e.message}")
            null
        }
    }

    suspend fun addTrackToPlaylist(playlistId: String, trackId: String): Boolean {
        return try {
            Log.d("PlaylistRepository", "Adding track $trackId to playlist $playlistId")
            apiService.addTrackToPlaylist(playlistId.toInt(), trackId.toInt())
            true
        } catch (e: Exception) {
            Log.e("PlaylistRepository", "Error adding track to playlist: ${e.message}")
            false
        }
    }


    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        apiService.removeTrackFromPlaylist(playlistId, trackId)
    }
}


data class CreatePlaylistRequest(
    val title: String
)


