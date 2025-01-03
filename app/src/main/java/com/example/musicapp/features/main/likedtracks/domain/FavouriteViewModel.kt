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
import com.example.musicapp.api.Album
//import com.example.musicapp.features.main.album.data.Album
import com.example.musicapp.features.main.album.data.AlbumRepository
import com.example.musicapp.features.main.artist.data.Artist
import com.example.musicapp.features.main.artist.data.ArtistRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow



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
    private val playlistRepository: PlaylistRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository
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

    private val _filteredArtist = MutableStateFlow<List<Artist>>(emptyList())
    val filteredArtist: StateFlow<List<Artist>> = _filteredArtist

    private val _artistSearchResults = MutableStateFlow<List<Artist>>(emptyList())
    val artistSearchResults: StateFlow<List<Artist>> = _artistSearchResults

    private val _filteredAlbums = MutableStateFlow<List<Album>>(emptyList())
    val filteredAlbums: StateFlow<List<Album>> = _filteredAlbums

    private val _albumSearchResults = MutableStateFlow<List<Album>>(emptyList())
    val albumSearchResults: StateFlow<List<Album>> = _albumSearchResults

    private val _playlistTracks = MutableStateFlow<List<Track>>(emptyList())
    val playlistTracks: StateFlow<List<Track>> = _playlistTracks

    private val _artistTracks = MutableStateFlow<List<Track>>(emptyList())
    val artistTracks: StateFlow<List<Track>> = _artistTracks

    private val _albumTracks = MutableStateFlow<List<Track>>(emptyList())
    val albumTracks: StateFlow<List<Track>> = _albumTracks

    private val _currentSourcePage = MutableStateFlow<String?>(null)
    val currentSourcePage: StateFlow<String?> = _currentSourcePage

    private val _currentPlaylistPage = MutableStateFlow<String>("")
    val currentPlaylistPage: StateFlow<String> = _currentPlaylistPage

    private var currentLikedTrackIndex: Int? = null

    private var currentPlaylistTrackIndex: Int? = null

    private var currentArtistTrackIndex: Int? = null

    private var currentAlbumTrackIndex: Int? = null

    fun setCurrentSourcePage(page: String) {
        _currentSourcePage.value = page
    }

    fun setCurrentPlaylistPage(id: String) {
        _currentPlaylistPage.value = id
    }

    fun setCurrentPlaylistTracks(tracks:List<Track>) {
        _playlistTracks.value = tracks
    }

    fun setCurrentArtistTracks(tracks:List<Track>) {
        _artistTracks.value = tracks
    }

    fun setCurrentAlbumTracks(tracks:List<Track>) {
        _albumTracks.value = tracks
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

    fun updateCurrentPlaylistTrackIndex(trackId:String) {
        currentPlaylistTrackIndex = _playlistTracks.value.indexOfFirst { it.id == trackId }
        if (currentPlaylistTrackIndex == -1) {
            currentPlaylistTrackIndex = null
        }
    }

    fun updateCurrentArtistTrackIndex(trackId:String) {
        currentArtistTrackIndex = _artistTracks.value.indexOfFirst { it.id == trackId }
        if (currentArtistTrackIndex == -1) {
            currentArtistTrackIndex = null
        }
    }

    fun updateCurrentAlbumTrackIndex(trackId:String) {
        currentAlbumTrackIndex = _albumTracks.value.indexOfFirst { it.id == trackId }
        if (currentAlbumTrackIndex == -1) {
            currentAlbumTrackIndex = null
        }
    }




    private val _artistImages = MutableStateFlow<Map<String, String>>(emptyMap())
    val artistImages: StateFlow<Map<String, String>> = _artistImages


    suspend fun getArtistImageByAlbumId(albumId: String): String? {
        return try {
            // Отримуємо альбом по ID
            val album = getAlbumById(albumId)
            if (album != null) {
                // Отримуємо виконавця по ID виконавця з альбому
                val artist = getArtistById(album.artistId.toString())
                // Повертаємо URL зображення виконавця
                artist?.imageUrl
            } else {
                null // Якщо альбом не знайдено
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // Повертаємо null у разі помилки
        }
    }


    fun loadArtistImageByAlbumId(albumId: String) {
        viewModelScope.launch {
            val imageUrl = getArtistImageByAlbumId(albumId)
            if (imageUrl != null) {
                _artistImages.value = _artistImages.value.toMutableMap().apply {
                    put(albumId, imageUrl)
                }
            }
        }
    }




    init {
        loadTracks()
        loadFavourites(userId="1")
        loadArtists()
        loadAlbums()
    }

    fun loadTracks() {
        viewModelScope.launch {
            val tracks = likedTracksRepository.getTracks()
            _filteredTracks.value = tracks
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

    // artists
    fun loadArtists() {
        viewModelScope.launch {
            val artists = artistRepository.getArtists()
            _filteredArtist.value = artists
        }
    }

    suspend fun getArtistById(artistId: String): Artist? {
        return artistRepository.getArtistById(artistId)
    }


    fun searchArtists(query: String) {
        viewModelScope.launch {
            try {
                    val searchResults = artistRepository.searchArtists(query)
                    _artistSearchResults.value = searchResults // Оновіть стан пошук
            } catch (e: Exception) {
                Log.e("LikedTracksViewModel", "Error searching tracks: ${e.message}")
            }
        }
    }


    // albums

    fun loadAlbums() {
        viewModelScope.launch {
            val albums = albumRepository.getAlbums()
            _filteredAlbums.value = albums
        }
    }

    suspend fun getAlbumById(albumId: String): Album? {
        return albumRepository.getAlbumById(albumId)
    }

    fun searchAlbums(query: String) {
        viewModelScope.launch {
            try {
                val searchResults = albumRepository.searchAlbums(query)
                _albumSearchResults.value = searchResults // Оновіть стан пошуку
            } catch (e: Exception) {
                Log.e("LikedTracksViewModel", "Error searching albums: ${e.message}")
            }
        }
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
//        filterTracks(category) // Передаємо категорію для фільтрації
    }

    fun filterTracks() {
        viewModelScope.launch {
            val allTracks = likedTracksRepository.getTracks()
            _filteredTracks.value = allTracks.filter {
                it.title.contains(_searchQuery.value, ignoreCase = true) ||
                        it.artist_id.toString().contains(_searchQuery.value, ignoreCase = true)
            }
        }
    }

    fun filterAlbums() {
        viewModelScope.launch {
            val allAlbums = albumRepository.getAlbums()
            _filteredAlbums.value = allAlbums.filter {
                it.title.contains(_searchQuery.value, ignoreCase = true) ||
                        it.artistId.toString().contains(_searchQuery.value, ignoreCase = true)
            }
        }
    }

    fun filterArtists() {
        viewModelScope.launch {
            val allArtists = artistRepository.getArtists()
            _filteredArtist.value = allArtists.filter {
                it.name.contains(_searchQuery.value, ignoreCase = true)
            }
        }
    }

    fun filterComponents(category: String) {
        when (category) {
            "songs" -> {
                filterTracks()

            }
            "albums" -> {
                filterAlbums()
            }
            "artists" -> {
                filterArtists()
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
                if (sourcePage == "Playlist") {
                    updateCurrentPlaylistTrackIndex(track.id)
                }
                if (sourcePage == "Artist") {
                    updateCurrentArtistTrackIndex(track.id)
                }
                if (sourcePage == "Album") {
                    updateCurrentAlbumTrackIndex(track.id)
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

    private fun playNextAlbumTrack() {
        if (currentAlbumTrackIndex != null) {
            val nextIndex = currentAlbumTrackIndex!! + 1
            val tracks = _albumTracks.value
            if (nextIndex in tracks.indices) {
                val nextTrack = tracks[nextIndex]
                Log.d("LikedTracksViewModel", "Playing next favourite track: $nextTrack")
                playTrack(nextTrack, "Album")
            } else {
                Log.d("LikedTracksViewModel", "End of favourite tracks. Stopping playback.")
                _isPlaying.value = false
                _currentTrack.value = null
                mediaPlayerManager.pause()
            }
        } else {
            Log.e("LikedTracksViewModel", "Current album track index is null.")
        }
    }

    private fun playNextArtistTrack() {
        if (currentArtistTrackIndex != null) {
            val nextIndex = currentArtistTrackIndex!! + 1
            val tracks = _artistTracks.value
            if (nextIndex in tracks.indices) {
                val nextTrack = tracks[nextIndex]
                Log.d("LikedTracksViewModel", "Playing next favourite track: $nextTrack")
                playTrack(nextTrack, "Artist")
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
            Log.e("LikedTracksViewModel", "Current artist track index is null.")
        }
    }

    private fun playNextPlaylistTrack() {
        if (currentPlaylistTrackIndex != null) {
            val nextIndex = currentPlaylistTrackIndex!! + 1
            val tracks = _playlistTracks.value
            if (nextIndex in tracks.indices) {
                val nextTrack = tracks[nextIndex]
                Log.d("LikedTracksViewModel", "Playing next favourite track: $nextTrack")
                playTrack(nextTrack, "Playlist")
            } else {
                Log.d("LikedTracksViewModel", "End of favourite tracks. Stopping playback.")
                _isPlaying.value = false
                _currentTrack.value = null
                mediaPlayerManager.pause()
            }
        } else {
            Log.e("LikedTracksViewModel", "Current playlist track index is null. ${_playlistTracks.value}")
        }
    }

    private fun playNextTrack() {
        when (_currentSourcePage.value) {
            "Favourite" -> playNextFavouriteTrack()
            "Playlist" -> playNextPlaylistTrack()
            "Artist" -> playNextArtistTrack()
            "Album" -> playNextAlbumTrack()
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

    fun hasNextTrack(): Boolean {
        return when (_currentSourcePage.value) {
            "Favourite" -> {
                currentLikedTrackIndex?.let { it + 1 < _likedTracksState.value.tracks.size } ?: false
            }
            "Playlist" -> {
                currentPlaylistTrackIndex?.let { it + 1 < _playlistTracks.value.size } ?: false
            }
            "Artist" -> {
                currentArtistTrackIndex != null && currentArtistTrackIndex!! < _artistTracks.value.size - 1
            }
            "Album" -> {
                currentAlbumTrackIndex != null && currentAlbumTrackIndex!! < _albumTracks.value.size - 1
            }
            else -> false
        }
    }


    private fun startUpdatingPosition() {
        viewModelScope.launch {
            while (_isPlaying.value) {
                _currentPosition.value = mediaPlayerManager.getCurrentPosition()
                delay(1000)  // Оновлюємо позицію кожну секунду
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

    private fun playPreviousAlbumTrack() {
        if (currentAlbumTrackIndex != null) {
            val previousIndex = currentAlbumTrackIndex!! - 1
            val tracks = _albumTracks.value
            if (previousIndex in tracks.indices) {
                val previousTrack = tracks[previousIndex]
                Log.d("LikedTracksViewModel", "Playing previous favourite track: $previousTrack")
                playTrack(previousTrack, "Favourite")
            } else {
                Log.d("LikedTracksViewModel", "Start of favourite tracks.")
            }
        } else {
            Log.e("LikedTracksViewModel", "Current album track index is null.")
        }
    }

    private fun playPreviousArtistTrack() {
        if (currentArtistTrackIndex != null) {
            val previousIndex = currentArtistTrackIndex!! - 1
            val tracks = _artistTracks.value
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

    private fun playPreviousPlaylistTrack() {
        if (currentPlaylistTrackIndex != null) {
            val previousIndex = currentPlaylistTrackIndex!! - 1
            val tracks = _playlistTracks.value
            if (previousIndex in tracks.indices) {
                val previousTrack = tracks[previousIndex]
                Log.d("LikedTracksViewModel", "Playing previous favourite track: $previousTrack")
                playTrack(previousTrack, "Playlist")
            } else {
                Log.d("LikedTracksViewModel", "Start of favourite tracks.")
            }
        } else {
            Log.e("LikedTracksViewModel", "Current playlist track index is null.")
        }
    }

    private fun playPreviousTrack() {
        when (_currentSourcePage.value) {
            "Favourite" -> playPreviousFavouriteTrack()
            "Playlist" -> playPreviousPlaylistTrack()
            "Artist" -> playPreviousArtistTrack()
            "Album" -> playPreviousAlbumTrack()
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
        viewModelScope.launch {
            // Оновлюємо поточну позицію
            _currentPosition.value = position.toInt()

            // Викликаємо метод для перемотування треку в MediaPlayerManager
            mediaPlayerManager.seekTo(position)

            // Якщо потрібно, можна оновити інші стани або виконати додаткові дії.
        }
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
