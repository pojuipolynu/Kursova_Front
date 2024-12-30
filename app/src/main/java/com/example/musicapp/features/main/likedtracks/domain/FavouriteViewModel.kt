package com.example.musicapp.features.main.likedtracks.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.features.main.likedtracks.data.LikedTracksRepository
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.playlists.data.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.media.MediaPlayer
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import android.util.Log


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
    private val mediaPlayerManager: MediaPlayerManager,
    private val playlistRepository: PlaylistRepository
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

    private val _playlistTracks = MutableStateFlow<List<Track>>(emptyList())
    val playlistTracks: StateFlow<List<Track>> = _playlistTracks

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults: StateFlow<List<Track>> = _searchResults

    private val _currentSourcePage = MutableStateFlow<String?>(null)
    val currentSourcePage: StateFlow<String?> = _currentSourcePage

    private val _currentPlaylistPage = MutableStateFlow<String>("")
    val currentPlaylistPage: StateFlow<String> = _currentPlaylistPage

    private var currentLikedTrackIndex: Int? = null

    fun setCurrentSourcePage(page: String) {
        _currentSourcePage.value = page
    }

    fun setCurrentPlaylistPage(id: String) {
        _currentPlaylistPage.value = id
    }


    fun updateCurrentLikedTrackIndex(trackId:String) {
        currentLikedTrackIndex = _likedTracksState.value.tracks.indexOfFirst {
            it.id == trackId
        }
        if (currentLikedTrackIndex == -1) {
            Log.e("LikedTracksViewModel", "Track not found in liked tracks.")
            Log.e("LikedTracksViewModel", "${_likedTracksState.value.tracks}")
            currentLikedTrackIndex = null
        }
    }




    init {
        loadTracks()
        loadFavourites(userId="1")
    }

    fun loadTracks() {
        viewModelScope.launch {
            val tracks = likedTracksRepository.getTracks()
            _filteredTracks.value = tracks
        }
    }

    fun loadPlaylistTracks() {
        viewModelScope.launch {
           val tracks = playlistRepository.getPlaylistTracksFlow(_currentPlaylistPage.value)
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
                loadFavourites(userId)
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


    fun playTrack(track: Track, sourcePage: String) {
        viewModelScope.launch {
            if (_currentTrack.value?.id != track.id) {
                mediaPlayerManager.stop()
                if (sourcePage == "Favourite") {
                    updateCurrentLikedTrackIndex(track.id)
                }
                mediaPlayerManager.play(track.fileUrl) {
                    playNextTrack()
                }
                _currentTrack.emit(track)
                _isPlaying.emit(true)
                _currentSourcePage.emit(sourcePage)

            } else if (!_isPlaying.value) {
                mediaPlayerManager.resume()
                _isPlaying.emit(true)
            }
        }
    }

    private fun playNextFavouriteTrack() {
        if (currentLikedTrackIndex != null) {
            val nextIndex = currentLikedTrackIndex!! + 1
            val tracks = _likedTracksState.value.tracks
            if (nextIndex in tracks.indices) {
                val nextTrack = tracks[nextIndex]
                Log.d("LikedTracksViewModel", "Playing next favourite track: $nextTrack")
                playTrack(nextTrack, "Favourite")
            } else {
                Log.d("LikedTracksViewModel", "End of favourite tracks. Stopping playback.")
                _isPlaying.value = false
                _currentTrack.value = null
                mediaPlayerManager.pause()
            }
        } else {
            Log.e("LikedTracksViewModel", "Current track index is null.")
        }
    }

    private fun playNextTrack() {
        when (_currentSourcePage.value) {
            "Favourite" -> playNextFavouriteTrack()
            else -> {
                val currentIndex = _filteredTracks.value.indexOfFirst { it.id == _currentTrack.value?.id }
                if (currentIndex != -1 && currentIndex + 1 < _filteredTracks.value.size) {
                    val nextTrack = _filteredTracks.value[currentIndex + 1]
                    playTrack(nextTrack, _currentSourcePage.value ?: "")
                } else {
                    _isPlaying.value = false
                    _currentTrack.value = null
                    mediaPlayerManager.pause()
                }
            }
        }
    }




    fun togglePlayPause(sourcePage: String) {
        if (_isPlaying.value) {
            mediaPlayerManager.pause()
            _isPlaying.value = false
        } else {
            _currentTrack.value?.let { track ->
                playTrack(track, sourcePage)
            }
        }
    }

    private fun playPreviousFavouriteTrack() {
        if (currentLikedTrackIndex != null) {
            val previousIndex = currentLikedTrackIndex!! - 1
            val tracks = _likedTracksState.value.tracks
            if (previousIndex in tracks.indices) {
                val previousTrack = tracks[previousIndex]
                Log.d("LikedTracksViewModel", "Playing previous favourite track: $previousTrack")
                playTrack(previousTrack, "Favourite")
            } else {
                Log.d("LikedTracksViewModel", "Start of favourite tracks.")
            }
        } else {
            Log.e("LikedTracksViewModel", "Current track index is null.")
        }
    }

    private fun playPreviousTrack() {
        when (_currentSourcePage.value) {
            "Favourite" -> playPreviousFavouriteTrack()
            else -> {
                val currentIndex = _filteredTracks.value.indexOfFirst { it.id == _currentTrack.value?.id }
                if (currentIndex != -1 && currentIndex - 1 >= 0) {
                    val previousTrack = _filteredTracks.value[currentIndex - 1]
                    playTrack(previousTrack, _currentSourcePage.value ?: "")
                } else {
                    Log.d("LikedTracksViewModel", "No previous track available.")
                }
            }
        }
    }

    fun skipToNextTrack() {
        playNextTrack()
    }

    fun skipToPreviousTrack() {
        playPreviousTrack()
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
    }
}


data class LikedTracksState(
    val tracks: List<Track> = emptyList(),
    val likedTrackIds: Set<String> = emptySet()
)
