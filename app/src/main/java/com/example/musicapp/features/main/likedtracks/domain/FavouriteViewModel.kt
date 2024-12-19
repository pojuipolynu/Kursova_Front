package com.example.musicapp.features.main.likedtracks.domain

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

    private var currentTrack: Track? = null
    private var isPlaying = false

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
        currentTrack = track
        isPlaying = true
        // Notify UI about playing state if needed
    }


    fun getTrackById(trackId: String): Track? {
        return likedTracksRepository.getTrackById(trackId)
    }
}

data class LikedTracksState(
    val tracks: List<Track>,
    val likedTrackIds: Set<String>
)