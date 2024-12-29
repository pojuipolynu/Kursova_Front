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
import kotlinx.coroutines.runBlocking
import android.util.Log


enum class TrackListMode {
    FAVORITES,
    SEARCH
}

@Singleton
class MediaPlayerManager @Inject constructor() {
    private var mediaPlayer: MediaPlayer? = null

    // Play track and notify when it's complete
    fun play(trackUrl: String, onCompletion: () -> Unit) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setOnCompletionListener {
                    onCompletion()  // Notify that the track has finished playing
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filteredTracks = MutableStateFlow<List<Track>>(emptyList())
    val filteredTracks: StateFlow<List<Track>> = _filteredTracks

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults: StateFlow<List<Track>> = _searchResults

    private var currentMode: String? = null

    fun setCurrentMode(mode: String) {
        currentMode = mode
    }

    init {
        loadTracks()
    }

    //Get tracks

    fun loadTracks() {
        viewModelScope.launch {
            val tracks = likedTracksRepository.getTracks()
            _filteredTracks.value = tracks
        }
    }

    suspend fun getTrackById(trackId: String): Track? {
        return likedTracksRepository.getTrackById(trackId)
    }

    fun getTrackByIdSync(trackId: String): Track? {
        return runBlocking {
            getTrackById(trackId)
        }
    }


    fun searchTracks(query: String) {
        viewModelScope.launch {
            try {
                val searchResults = likedTracksRepository.searchTracks(query)
                _searchResults.value = searchResults // Оновіть стан пошуку
            } catch (e: Exception) {
                Log.e("LikedTracksViewModel", "Error searching tracks: ${e.message}")
            }
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


    fun loadFavourites(userId: String) {
        viewModelScope.launch {
            try {
                val favourites = likedTracksRepository.getFavourites(userId)
                _likedTracksState.value = LikedTracksState(
                    tracks = favourites,
                    likedTrackIds = favourites.map { it.id.toString() }.toSet()
                )
            } catch (e: Exception) {
                Log.e("LikedTracksViewModel", "Error loading favourites: ${e.message}")
            }
        }
    }


    fun toggleLike(userId: String, trackId: String) {
        viewModelScope.launch {
            try {
                val isLiked = _likedTracksState.value.likedTrackIds.contains(trackId)
                Log.d("toggleLike", "isLiked: $isLiked, userId: $userId, trackId: $trackId")
                if (isLiked) {
                    Log.d("toggleLike", "Deleting favourite...")
                    likedTracksRepository.deleteFavourite(userId, trackId)
                } else {
                    Log.d("toggleLike", "Adding favourite...")
                    likedTracksRepository.addFavourite(userId, trackId)
                }
                loadFavourites(userId) // Оновлюємо список після змін
            } catch (e: Exception) {
                Log.e("LikedTracksViewModel", "Error toggling like: ${e.message}")
            }
        }
    }

    fun loadLikedTrackIds(userId: String) {
        viewModelScope.launch {
            try {
                val likedIds = likedTracksRepository.getLikedTrackIds(userId)
                _likedTracksState.value = _likedTracksState.value.copy(
                    likedTrackIds = likedIds
                )
            } catch (e: Exception) {
                Log.e("LikedTracksViewModel", "Error loading liked track IDs: ${e.message}")
            }
        }
    }


    fun playTrack(track: Track) {
        if (_currentTrack.value?.id != track.id) {
            mediaPlayerManager.stop()
            mediaPlayerManager.play(track.fileUrl) {
               playNextTrack()
            }
            _currentTrack.value = track
            _isPlaying.value = true
        } else if (!_isPlaying.value) {
            mediaPlayerManager.resume()
            _isPlaying.value = true
        }
    }


//    private fun playNextFavouriteTrack() {
//        val currentIndex = _likedTracksState.value.tracks.indexOfFirst { it.id == _currentTrack.value?.id }
//        if (currentIndex != -1 && currentIndex + 1 < _likedTracksState.value.tracks.size) {
//            val nextTrack = _likedTracksState.value.tracks[currentIndex + 1]
//            playTrack(nextTrack)
//        } else {
//            _isPlaying.value = false
//            _currentTrack.value = null
//        }
//    }


    private fun playNextTrack() {
        val currentIndex = _filteredTracks.value.indexOfFirst { it.id == _currentTrack.value?.id }
        if (currentIndex != -1 && currentIndex + 1 < _filteredTracks.value.size) {
            val nextTrack = _filteredTracks.value[currentIndex + 1]
            playTrack(nextTrack)
        } else {
            _isPlaying.value = false
            _currentTrack.value = null
        }
    }

    fun pauseTrack() {
        mediaPlayerManager.pause()
        _isPlaying.value = false
    }


    fun togglePlayPause() {
        if (_isPlaying.value) {
            mediaPlayerManager.pause()
            _isPlaying.value = false
        } else {
            _currentTrack.value?.let { track ->
                playTrack(track)
            }
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
    val tracks: List<Track> = emptyList(),
    val likedTrackIds: Set<String> = emptySet()
)
