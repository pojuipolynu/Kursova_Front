package com.example.musicapp.api

import com.example.musicapp.features.main.album.data.Album
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.playlists.data.CreatePlaylistRequest
import com.example.musicapp.features.main.profile.data.Playlist
import com.example.musicapp.features.main.artist.data.Artist
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Body

import com.google.gson.annotations.SerializedName
import retrofit2.http.PATCH


data class PlaylistResponse(
    @SerializedName("title") val title: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("id") val id: Int  // Change to Int instead of String
)


data class PlaylistTracksResponse(
    @SerializedName("songs") val tracks: List<Track>
)

data class SongsResponse(
    @SerializedName("songs") val songs: List<Track>
)

//data class Song(
//    @SerializedName("id") val id: Int,
//    @SerializedName("title") val title: String,
//    @SerializedName("artist") val artist: String,
//    @SerializedName("fileUrl") val fileUrl: String,
//    @SerializedName("imageUrl") val imageUrl: String,
//    @SerializedName("duration") val duration: String
//)

data class FavouriteRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("song_id") val songId: String
)

data class FavouritesResponse(
    @SerializedName("songs") val songs: List<Track>
)

data class SearchResponse(
    @SerializedName("songs") val songs: List<Track>
)

data class UserStatusResponse(
    @SerializedName("status") val status: String,
    @SerializedName("user_id") val userId: String
)


data class ArtistsResponse(
    @SerializedName("artists") val artists: List<Artist>
)

data class ArtistResponse(
    @SerializedName("name") val name: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("id") val id: Int
)

data class AlbumsResponse(
    @SerializedName("albums") val albums: List<Album>
)

data class AlbumResponse(
    @SerializedName("title") val title: String,
    @SerializedName("artist_id") val artistId: Int,
    @SerializedName("id") val id: Int
)

data class Album(
    @SerializedName("title") val title: String,
    @SerializedName("artist_id") val artistId: Int,
    @SerializedName("id") val id: Int
)



interface ApiService {
    @POST("user/status/{user_id}/set")
    suspend fun setUserStatus(
        @Path("user_id") userId: String,
    )

    @GET("user/status/{user_id}")
    suspend fun getUserStatus(
        @Path("user_id") userId: String
    ): UserStatusResponse

    @PATCH("user/status/{user_id}/change")
    suspend fun changeUserStatus(
        @Path("user_id") userId: String
    )

    @GET("user/playlists/{user_id}")
    suspend fun getUserPlaylists(
        @Path("user_id") userId: String
    ): List<Playlist>

    @GET("user/playlists/{user_id}/{playlist_id}")
    suspend fun getPlaylistDetails(
        @Path("user_id") userId: String,
        @Path("playlist_id") playlistId: Int
    ): PlaylistResponse


    // Create a playlist
    @POST("user/create/playlist/{user_id}")
    suspend fun createPlaylist(
        @Path("user_id") userId: String,
        @Body request: CreatePlaylistRequest
    ): PlaylistResponse


    // Add a track to a playlist
    @POST("user/playlist/{playlist_id}/songs/{song_id}")
    suspend fun addTrackToPlaylist(
        @Path("playlist_id") playlistId: Int,
        @Path("song_id") songId: Int
    )

    // Remove a track from a playlist
    @DELETE("user/playlist/{playlist_id}/songs/{song_id}")
    suspend fun removeTrackFromPlaylist(
        @Path("playlist_id") playlistId: String,
        @Path("song_id") songId: String
    )

    // Get tracks of a playlist
    @GET("user/playlist/{playlist_id}/songs")
    suspend fun getPlaylistTracks(
        @Path("playlist_id") playlistId: String
    ): PlaylistTracksResponse


    @GET("songs")
    suspend fun getTracks(): SongsResponse

    @GET("songs/{trackId}")
    suspend fun getTrackById(@Path("trackId") trackId: String): Track

    @GET("songs/search/{query}")
    suspend fun searchTracks(@Path("query") query: String): SearchResponse


    @GET("songs/favourites/{user_id}")
    suspend fun getFavourites(@Path("user_id") userId: String): FavouritesResponse

    @POST("songs/favourites")
    suspend fun addFavourite(@Body favourite: FavouriteRequest)

    @DELETE("songs/favourites/{user_id}/{song_id}")
    suspend fun deleteFavourite(
        @Path("user_id") userId: String,
        @Path("song_id") songId: String
    )

    // artists
    @GET("other/artists")
    suspend fun getArtists(): ArtistsResponse

    @GET("other/artists/search/{artistId}")
    suspend fun getArtistById(@Path("artistId") artistId: String): Artist

    @GET("other/artists/search_name/{query}")
    suspend fun searchArtists(@Path("query") query: String): ArtistsResponse


    // albums
    @GET("other/albums")
    suspend fun getAlbums(): AlbumsResponse

    @GET("/other/albums/search/{albumId}")
    suspend fun getAlbumById(@Path("albumId") albumId: String): Album

    @GET("/other/albums/search_name/{query}")
    suspend fun searchAlbums(@Path("query") query: String): AlbumsResponse


    @GET("other/artists/search/{artist_id}")
    suspend fun getArtistById(@Path("artist_id") artistId: Int): ArtistResponse

    @GET("other/artists/albums/{artist_id}")
    suspend fun getAlbumsByArtist(@Path("artist_id") artistId: Int): AlbumsResponse

    @GET("other/artists/songs/{artist_id}")
    suspend fun getSongsByArtist(@Path("artist_id") artistId: Int): SongsResponse

    @GET("/other/albums/search/{album_id}")
    suspend fun getAlbumById(@Path("album_id") albumId: Int): AlbumResponse

    @GET("other/albums/songs/{album_id}")
    suspend fun getSongsByAlbum(@Path("album_id") albumId: Int): SongsResponse
}





