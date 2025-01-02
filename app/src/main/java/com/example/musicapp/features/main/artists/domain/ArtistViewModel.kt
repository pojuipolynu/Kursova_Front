package com.example.musicapp.features.main.artists.domain

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.api.Album
import com.example.musicapp.api.AlbumResponse
import com.example.musicapp.features.main.artists.data.*
import com.example.musicapp.features.main.likedtracks.data.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val artistRepository: ArtistRepository
) : ViewModel() {

    val artistState: StateFlow<ArtistResult> = artistRepository.artistState

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _albumDetails = MutableStateFlow<AlbumResponse?>(null)
    val albumDetails: StateFlow<AlbumResponse?> = _albumDetails.asStateFlow()

    private val _albumTracks = MutableStateFlow<List<Track>>(emptyList())
    val albumTracks: StateFlow<List<Track>> = _albumTracks.asStateFlow()

    fun fetchArtistDetails(artistId: String) {
        viewModelScope.launch {
            try {
                artistRepository.getArtistDetails(artistId)
            } catch (e: Exception) {
                Log.e("ArtistViewModel", "Error fetching artist details: ${e.message}")
            }
        }
    }

    fun fetchArtistAlbums(artistId: String) {
        viewModelScope.launch {
            _albums.value = artistRepository.getArtistAlbums(artistId)
        }
    }

    fun fetchArtistTracks(artistId: String) {
        viewModelScope.launch {
            _tracks.value = artistRepository.getArtistTracks(artistId)
        }
    }

    fun fetchAlbumDetails(albumId: String) {
        viewModelScope.launch {
            try {
                val details = artistRepository.getAlbumDetails(albumId)
                _albumDetails.value = details
            } catch (e: Exception) {
                Log.e("ArtistViewModel", "Error fetching album details: ${e.message}")
            }
        }
    }

    fun fetchAlbumTracks(albumId: String) {
        viewModelScope.launch {
            _albumTracks.value = artistRepository.getAlbumTracks(albumId)
        }
    }
}

