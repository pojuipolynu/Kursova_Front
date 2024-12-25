package com.example.musicapp.features.main.likedtracks.domain

//import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.features.main.likedtracks.data.LikedTracksRepository
import com.example.musicapp.features.main.likedtracks.data.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.media.MediaPlayer
import javax.inject.Singleton

@Singleton
class MediaPlayerManager @Inject constructor() {
    private var mediaPlayer: MediaPlayer? = null

    fun play(trackUrl: String, onCompletion: () -> Unit) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setOnCompletionListener {
                    onCompletion()
                }
            }
        } else {
            mediaPlayer?.reset()
        }

        mediaPlayer?.apply {
            setDataSource(trackUrl)
            prepare()
            start()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }
}


@HiltViewModel
class LikedTracksViewModel @Inject constructor(
    private val likedTracksRepository: LikedTracksRepository,
    private val mediaPlayerManager: MediaPlayerManager
) : ViewModel() {

    private val _likedTracksState = MutableStateFlow(LikedTracksState(emptyList(), emptySet()))
    val likedTracksState: StateFlow<LikedTracksState> = _likedTracksState

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition

//    private var mediaPlayer: MediaPlayer? = null

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

            // Оновлення списку треків у стані
            val updatedLikedIds = likedTracksRepository.getLikedTrackIds().toSet()
            val updatedTracks = _likedTracksState.value.tracks.filter { updatedLikedIds.contains(it.id) }

            _likedTracksState.value = _likedTracksState.value.copy(
                tracks = updatedTracks,
                likedTrackIds = updatedLikedIds
            )
        }
    }


    fun playTrack(track: Track) {
        if (_currentTrack.value?.id != track.id) {
            mediaPlayerManager.play(track.fileUrl) {
                _isPlaying.value = false
            }
            _currentTrack.value = track
            _isPlaying.value = true
        } else if (!_isPlaying.value) {
            mediaPlayerManager.resume()
            _isPlaying.value = true
        }
    }

    fun pauseTrack() {
        mediaPlayerManager.pause()
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
        mediaPlayerManager.seekTo(position)
        _currentPosition.value = position
    }

    fun updateCurrentPosition() {
        _currentPosition.value = mediaPlayerManager.getCurrentPosition()
    }

    fun isTrackPlaying(track: Track): Boolean {
        return _currentTrack.value?.id == track.id && _isPlaying.value
    }

    fun getTrackById(trackId: String): Track? {
        return likedTracksRepository.getTrackById(trackId)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayerManager.getCurrentPosition()
    }

    fun getDuration(): Int {
        return mediaPlayerManager.getDuration()
    }

    override fun onCleared() {
        super.onCleared()
//        mediaPlayer?.release()
//        mediaPlayer = null
    }
}

data class LikedTracksState(
    val tracks: List<Track>,
    val likedTrackIds: Set<String>
)
