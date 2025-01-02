//package com.example.musicapp.features.main.artist.domain
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.musicapp.features.main.artist.data.ArtistRepository
//import com.example.musicapp.features.main.artist.data.Artist
//import com.example.musicapp.features.main.likedtracks.data.Track
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class ArtistsViewModel @Inject constructor(
//    private val artistRepository: ArtistRepository,
//) : ViewModel(){
//    private val _filteredArtist = MutableStateFlow<List<Artist>>(emptyList())
//    val filteredArtist: StateFlow<List<Artist>> = _filteredArtist
//
//    private val _artistSearchResults = MutableStateFlow<List<Artist>>(emptyList())
//    val artistSearchResults: StateFlow<List<Artist>> = _artistSearchResults
//
//    fun loadArtists() {
//        viewModelScope.launch {
//            val artists = artistRepository.getArtists()
//            _filteredArtist.value = artists
//        }
//    }
//
//    suspend fun getArtistById(artistId: String): Artist? {
//        return artistRepository.getArtistById(artistId)
//    }
//
//    fun searchArtists(query: String, category: String) {
//        viewModelScope.launch {
//            try {
//                val artistSearchResults = artistRepository.searchArtists(query)
//                _artistSearchResults.value = artistSearchResults // Оновіть стан пошук
//            } catch (e: Exception) {
//                Log.e("LikedTracksViewModel", "Error searching tracks: ${e.message}")
//            }
//        }
//    }
//}