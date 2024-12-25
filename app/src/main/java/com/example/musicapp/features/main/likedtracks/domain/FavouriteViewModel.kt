package com.example.musicapp.features.main.likedtracks.domain

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.features.main.likedtracks.data.LikedTracksRepository
import com.example.musicapp.features.main.likedtracks.data.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikedTracksViewModel @Inject constructor(
    private val likedTracksRepository: LikedTracksRepository
) : ViewModel() {

    private val _likedTracksState = MutableStateFlow(LikedTracksState(emptyList(), emptySet()))
    val likedTracksState: StateFlow<LikedTracksState> = _likedTracksState

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition

    private var mediaPlayer: MediaPlayer? = null

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filteredTracks = MutableStateFlow<List<Track>>(emptyList())
    val filteredTracks: StateFlow<List<Track>> = _filteredTracks

    init {
        loadTracks()
    }

    fun loadTracks() {
        viewModelScope.launch {
            val tracks = likedTracksRepository.getTracks()
            _filteredTracks.value = tracks
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterTracks()
    }

    fun filterTracks() {
        viewModelScope.launch {
            val allTracks = likedTracksRepository.getTracks()
            _filteredTracks.value = allTracks.filter {
                it.title.contains(_searchQuery.value, ignoreCase = true) ||
                        it.artist.contains(_searchQuery.value, ignoreCase = true)
            }
        }
    }
    fun loadLikedTracks() {
        viewModelScope.launch {
            val tracks = likedTracksRepository.getTracks()
            val likedIds = likedTracksRepository.getLikedTrackIds()
            _likedTracksState.value = LikedTracksState(tracks, likedIds)
        }
    }

    fun toggleLike(trackId: String) {
        viewModelScope.launch {
            likedTracksRepository.toggleLike(trackId)
            loadLikedTracks()
        }
    }

    fun playTrack(track: Track) {
        if (_currentTrack.value == track && _isPlaying.value) {
            // If the same track is already playing, do nothing
            return
        }

        // Release the previous MediaPlayer
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(track.fileUrl)
            prepare()
            start()
        }

        mediaPlayer?.setOnCompletionListener {
            _isPlaying.value = false
        }

        _currentTrack.value = track
        _isPlaying.value = true
    }

    fun pauseTrack() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pauseTrack()
        } else {
            _currentTrack.value?.let { playTrack(it) }
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _currentPosition.value = position
    }

    fun updateCurrentPosition() {
        mediaPlayer?.let {
            _currentPosition.value = it.currentPosition
        }
    }

    fun isTrackPlaying(track: Track): Boolean {
        return _currentTrack.value?.id == track.id && _isPlaying.value
    }

    fun getTrackById(trackId: String): Track? {
        return likedTracksRepository.getTrackById(trackId)
    }

    fun getMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

data class LikedTracksState(
    val tracks: List<Track>,
    val likedTrackIds: Set<String>
)
