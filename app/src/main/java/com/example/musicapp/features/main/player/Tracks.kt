//// File: MusicPlayerUI.kt
//package com.example.musicapp.shared
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material.icons.filled.PlayArrow
//import androidx.compose.material.icons.outlined.FavoriteBorder
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import coil.compose.rememberAsyncImagePainter
//
//@Composable
//fun TrackPlayerScreen(
//    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel()
//) {
//    val currentTrack by musicPlayerViewModel.currentTrack.collectAsState()
//    val isPlaying by musicPlayerViewModel.isPlaying.collectAsState()
//
//    currentTrack?.let { track ->
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(16.dp))
//            Image(
//                painter = rememberAsyncImagePainter(model = track.imageUrl),
//                contentDescription = "Track Image",
//                modifier = Modifier.size(200.dp)
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = track.title,
//                fontWeight = FontWeight.Bold,
//                fontSize = 24.sp
//            )
//            Text(
//                text = track.artist,
//                fontSize = 18.sp,
//                color = Color.Gray
//            )
//            Button(onClick = { musicPlayerViewModel.togglePlayPause() }) {
//                Text(if (isPlaying) "Pause" else "Play")
//            }
//        }
//    }
//}
//
//@Composable
//fun BottomTrackBar(
//    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel(),
//    onTrackClick: (Track) -> Unit
//) {
//    val currentTrack by musicPlayerViewModel.currentTrack.collectAsState()
//    val isPlaying by musicPlayerViewModel.isPlaying.collectAsState()
//
//    currentTrack?.let { track ->
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.DarkGray)
//                .padding(16.dp)
//                .clickable { onTrackClick(track) },
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Image(
//                painter = rememberAsyncImagePainter(model = track.imageUrl),
//                contentDescription = "Track Image",
//                modifier = Modifier.size(48.dp)
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = track.title,
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = track.artist,
//                    color = Color.Gray
//                )
//            }
//            IconButton(onClick = { musicPlayerViewModel.togglePlayPause() }) {
//                Icon(
//                    imageVector = Icons.Default.PlayArrow,
//                    contentDescription = "Play/Pause",
//                    tint = Color.White
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun TrackRow(
//    track: Track,
//    isLiked: Boolean,
//    onLikeClick: () -> Unit,
//    onTrackClick: () -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onTrackClick() }
//            .padding(8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Image(
//            painter = rememberAsyncImagePainter(model = track.imageUrl),
//            contentDescription = "Track Image",
//            modifier = Modifier
//                .size(60.dp)
//                .clip(RoundedCornerShape(4.dp))
//                .padding(end = 8.dp)
//        )
//        Column(
//            modifier = Modifier.weight(1f)
//        ) {
//            Text(
//                text = track.title,
//                color = Color.White,
//                fontWeight = FontWeight.Bold
//            )
//            Text(
//                text = track.artist,
//                color = Color.Gray
//            )
//        }
//        IconButton(onClick = onLikeClick) {
//            Icon(
//                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
//                contentDescription = if (isLiked) "Unlike" else "Like",
//                tint = if (isLiked) Color.Red else Color.Gray
//            )
//        }
//    }
//}
