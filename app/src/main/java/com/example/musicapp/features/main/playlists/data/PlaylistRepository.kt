package com.example.musicapp.features.main.playlists.data

import com.example.musicapp.api.ApiService
import com.example.musicapp.api.PlaylistResponse
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.profile.data.Playlist
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

    fun getPlaylistName(userId: String, playlistId: String): String {
        return try {
            val name = apiService.getPlaylistDetails(userId, playlistId).title
            name ?: "Unknown Playlist"  // Provide a fallback name if it is null
        } catch (e: Exception) {
            "Error loading playlist name"  // Return a fallback name in case of an error
        }
    }


    fun getPlaylistTracksFlow(playlistId: String): Flow<List<Track>> {
        return flow {
            val response = apiService.getPlaylistTracks(playlistId)
            emit(response.tracks)
        }
    }

    suspend fun createPlaylist(userId: String, name: String): PlaylistResponse {
        return apiService.createPlaylist(userId, name)
    }

    suspend fun addTrackToPlaylist(playlistId: String, trackId: String) {
        apiService.addTrackToPlaylist(playlistId, trackId)
    }

    suspend fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        apiService.removeTrackFromPlaylist(playlistId, trackId)
    }
}

