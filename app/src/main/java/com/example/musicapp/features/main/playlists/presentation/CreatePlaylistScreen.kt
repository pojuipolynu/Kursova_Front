package com.example.musicapp.features.main.playlists.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.search.presentation.TrackRow
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.White80
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistScreen(
//    viewModel: PlaylistViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPlaylist: (String) -> Unit
) {
    val playlistName = remember { mutableStateOf("") }
    val addedTracks = remember { mutableStateListOf<Track>() }
//    val allTracks by viewModel.allTracks.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(Black90)) {
        TopAppBar(
            title = { Text("Create Playlist", color = White80) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = White80)
                }
            },
            actions = {
//                IconButton(onClick = {
//                    scope.launch {
//                        val playlistId = viewModel.createPlaylist(playlistName.value)
//                        addedTracks.forEach { track -> viewModel.addTrackToPlaylist(playlistId, track.id) }
//                        onNavigateToPlaylist(playlistId)
//                    }
//                }) {
//                    Icon(Icons.Filled.Check, contentDescription = "Save", tint = White80)
//                }
            }
        )

//        TextField(
//            value = playlistName.value,
//            onValueChange = { playlistName.value = it },
//            placeholder = { Text("Enter playlist name") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            colors = TextFieldDefaults.textFieldColors(
//                textColor = White80,
//                containerColor = Color.DarkGray
//            )
//        )
//
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(allTracks.size) { index ->
//                val track = allTracks[index]
//                TrackRow(
//                    track = track,
//                    isAdded = addedTracks.contains(track),
//                    onAdd = { addedTracks.add(track) },
//                    onRemove = { addedTracks.remove(track) }
//                )
//            }
//        }
    }
}
