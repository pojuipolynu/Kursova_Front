package com.example.musicapp.features.main.likedtracks.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikedTracksRepository @Inject constructor() {
    private val tracks = listOf(
        Track("1", "https://f005.backblazeb2.com/file/music-app-files/covers/SheWantsRevenge.st.jpg", "Sister", "She wants revenge", "4:19", "https://f005.backblazeb2.com/file/music-app-files/music/She+Wants+Revenge-Sister.mp3"),
        Track("2", "https://f005.backblazeb2.com/file/music-app-files/covers/artworks-bQyGQjPCgQ4FIIAo-WbjuKg-t500x500.jpeg", "Billie Eilish", "My strange addiction", "2:59", "https://f005.backblazeb2.com/file/music-app-files/music/billie-eilish_-_my-strange-addiction.mp3"),
        Track("3", "https://f005.backblazeb2.com/file/music-app-files/covers/500x500.jpg", "Your love", "She wants revenge", "3:36", "https://f005.backblazeb2.com/file/music-app-files/music/She+Wants+Revenge-Your+Love.mp3"),
        Track("4", "https://f005.backblazeb2.com/file/music-app-files/covers/1_-JYPeNvOdIO52Z9TuV0Wzg.jpg", "The Hills", "The Weekend", "4:02", "https://f005.backblazeb2.com/file/music-app-files/music/The+Weeknd-The+Hills.mp3")
    )
    private val likedTrackIds = mutableSetOf<String>()

    fun getTracks(): List<Track> = tracks

    fun getLikedTrackIds(): Set<String> = likedTrackIds

    fun getTrackById(trackId: String): Track? {
        return tracks.find { it.id == trackId }
    }

    suspend fun toggleLike(trackId: String) {
        if (likedTrackIds.contains(trackId)) {
            likedTrackIds.remove(trackId)
        } else {
            likedTrackIds.add(trackId)
        }
    }
}

data class Track(
    val id: String,
    val imageUrl: String,
    val title: String,
    val artist: String,
    val duration: String,
    val fileUrl: String
)