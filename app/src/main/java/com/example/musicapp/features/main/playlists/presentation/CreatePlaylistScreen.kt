package com.example.musicapp.features.main.playlists.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.auth.domain.AuthViewModel
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.playlists.domain.PlaylistViewModel
import com.example.musicapp.features.main.search.presentation.TrackRow
import com.example.musicapp.ui.theme.Black80
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.Red60
import com.example.musicapp.ui.theme.White80
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistScreen(
    likedTracksViewModel: LikedTracksViewModel,
    authViewModel: AuthViewModel = hiltViewModel(),
    playlistViewModel: PlaylistViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()
    val scope = rememberCoroutineScope()

    val playlistName = remember { mutableStateOf("") }
    val selectedTracks = remember { mutableStateOf(setOf<String>()) }


    LaunchedEffect(Unit) {
        authViewModel.getCurrentUserId()?.let { userId ->
            likedTracksViewModel.loadFavourites(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
    ) {
        HeaderComponent(text = "Створення плейлисту")

        Spacer(modifier = Modifier.height(16.dp))

        // Input field for playlist name
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black80, RoundedCornerShape(4.dp)),
            ) {
                OutlinedTextField(
                    value = playlistName.value,
                    onValueChange = { playlistName.value = it },
                    placeholder = {
                        Text(
                            text = "Назва плейлисту",
                            color = White80.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Black80, RoundedCornerShape(4.dp)),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = White80,
                        focusedBorderColor = Red60,
                    ),
                )

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(likedTracksState.tracks) { track ->
                val isSelected = selectedTracks.value.contains(track.id)
                AddTrackRow(
                    track = track,
                    isSelected = isSelected,
                    onTrackClick = {
                        if (isSelected) {
                            selectedTracks.value -= track.id
                        } else {
                            selectedTracks.value += track.id
                        }
                        Log.d("CreatePlaylistScreen", "Track clicked: ${track.title}")
                        Log.d("CreatePlaylistScreen", "Selected tracks: ${selectedTracks.value}")
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create Playlist Button
        Button(
            onClick = {
                authViewModel.getCurrentUserId()?.let { userId ->
                    scope.launch {
                        // Step 1: Create playlist
                        val response = playlistViewModel.createPlaylist(userId, playlistName.value)
                        Log.d("CreatePlaylistScreen", "Playlist created: $response")
                        response?.let { playlistResponse ->
                            // Step 2: Add tracks to playlist
                            selectedTracks.value.forEach { trackId ->
                                playlistViewModel.addTrackToPlaylist(playlistResponse.toString(), trackId)
                            }
                        }
                        onNavigateBack()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Red60,
                contentColor = Red60
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Створити плейлист",
                style = MaterialTheme.typography.bodySmall,
                color = White80,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


@Composable
fun AddTrackRow(
    track: Track,
    isSelected: Boolean,
    onTrackClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = track.imageUrl),
            contentDescription = "Track Image",
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .padding(end = 8.dp),
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.title,
                color = White80,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = track.artist_id.toString(),
                color = White80,
                style = MaterialTheme.typography.labelSmall
            )
        }
        IconButton(
            onClick = onTrackClick
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.Remove else Icons.Default.Add,
                contentDescription = if (isSelected) "Remove" else "Add",
                tint = if (isSelected) Color.Red else Color.Gray
            )
        }
    }
}


