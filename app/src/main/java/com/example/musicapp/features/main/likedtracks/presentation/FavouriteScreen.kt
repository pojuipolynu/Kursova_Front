package com.example.musicapp.features.main.likedtracks.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.ui.theme.White80
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import com.example.musicapp.features.main.BottomTrackBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    likedTracksViewModel: LikedTracksViewModel = hiltViewModel(),
    onTrackClick: (String) -> Unit
) {
    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()
    val currentTrack by likedTracksViewModel.currentTrack.collectAsState()
    val isPlaying by likedTracksViewModel.isPlaying.collectAsState()

    LaunchedEffect(Unit) {
        likedTracksViewModel.loadLikedTracks()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HeaderComponent(text = "Ваша медіатека")

//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = "",
//                onValueChange = {},
//                placeholder = {
//                    Text(
//                        text = "Пошук",
//                        color = White80,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.DarkGray, RoundedCornerShape(4.dp)),
//                colors = TextFieldDefaults.outlinedTextFieldColors(
//                    cursorColor = Color.White,
//                    unfocusedTextColor = White80,
//                    unfocusedPrefixColor = White80,
//                    focusedBorderColor = Color.Transparent,
//                    unfocusedBorderColor = Color.Transparent
//                ),
//                leadingIcon = {
//                    Icon(
//                        imageVector = Icons.Default.Search,
//                        contentDescription = "Search Icon",
//                        tint = White80
//                    )
//                }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                ActionButton(text = "Відтворити", icon = Icons.Default.PlayArrow)
//                ActionButton(text = "Тасувати", icon = Icons.Default.PlayArrow)
//            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(likedTracksState.tracks) { track ->
                    TrackRow(
                        track = track,
                        isLiked = likedTracksState.likedTrackIds.contains(track.id),
                        onLikeClick = { likedTracksViewModel.toggleLike(track.id) },
                        onTrackClick = {
                            likedTracksViewModel.playTrack(track)
                            onTrackClick(track.id)
                        }
                    )
                }
            }
        }

        currentTrack?.let { track ->
            BottomTrackBar(
                track = track,
                isPlaying = isPlaying,
                onPlayClick = {
                    likedTracksViewModel.togglePlayPause()
                },
                onTrackClick = {
                    onTrackClick(track.id)
                }
            )
        }
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector) {
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C3E3E)),
        modifier = Modifier
            .width(200.dp)
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun TrackRow(
    track: Track,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onTrackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick() }
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
                text = track.artist,
                color = White80,
                style = MaterialTheme.typography.labelSmall
            )
        }
        IconButton(
            onClick = onLikeClick
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = if (isLiked) Color.Red else Color.Gray
            )
        }
    }
}
//
//@Composable
//fun TrackPlayerScreen(
//    track: Track,
//    likedTracksViewModel: LikedTracksViewModel = hiltViewModel()
//) {
//    val currentTrack by likedTracksViewModel.currentTrack.collectAsState()
//    val isPlaying by likedTracksViewModel.isPlaying.collectAsState()
//    val currentPosition by likedTracksViewModel.currentPosition.collectAsState()
//
//    LaunchedEffect(currentTrack) {
//        while (isActive) { // Перевіряємо, чи не завершено LaunchedEffect
//            likedTracksViewModel.updateCurrentPosition()
//            delay(1000) // Оновлюємо кожну секунду
//        }
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Spacer(modifier = Modifier.height(16.dp))
//        Image(
//            painter = rememberAsyncImagePainter(model = track.imageUrl),
//            contentDescription = "Track Image",
//            modifier = Modifier.size(200.dp),
//            contentScale = ContentScale.Crop
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(
//            text = track.title,
//            fontWeight = FontWeight.Bold,
//            fontSize = 24.sp
//        )
//        Text(
//            text = track.artist,
//            fontSize = 18.sp,
//            color = Color.Gray
//        )
//        Slider(
//            value = currentPosition.toFloat(),
//            onValueChange = { newPosition ->
//                likedTracksViewModel.seekTo(newPosition.toInt())
//            },
//            valueRange = 0f..(likedTracksViewModel.getMediaPlayer()?.duration?.toFloat() ?: 1f)
//        )
//        Button(onClick = {
//            likedTracksViewModel.togglePlayPause()
//        }) {
//            Text(if (isPlaying) "Pause" else "Play")
//        }
//    }
//}

