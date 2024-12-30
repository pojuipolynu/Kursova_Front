package com.example.musicapp.features.main.playlists.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.playlists.data.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val repository: PlaylistRepository
) : ViewModel() {

    val allTracks: Flow<List<Track>> = repository.allTracks

    fun getPlaylistTracks(playlistId: String): Flow<List<Track>> {
        return repository.getPlaylistTracksFlow(playlistId)
    }

    fun getPlaylistName(userId: String, playlistId: String): String {
        return repository.getPlaylistName(userId, playlistId)
    }

    suspend fun createPlaylist(userId: String, name: String): String {
        val response = repository.createPlaylist(userId, name)
        return response.id.toString()
    }

    fun addTrackToPlaylist(playlistId: String, trackId: String) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlistId, trackId)
        }
    }

    fun removeTrackFromPlaylist(playlistId: String, trackId: String) {
        viewModelScope.launch {
            repository.removeTrackFromPlaylist(playlistId, trackId)
        }
    }
}

