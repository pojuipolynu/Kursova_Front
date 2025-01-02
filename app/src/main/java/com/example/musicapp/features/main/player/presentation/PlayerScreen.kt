package com.example.musicapp.features.main.player.presentation

import VolumeObserver
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.musicapp.features.main.likedtracks.data.Track
import com.example.musicapp.features.main.likedtracks.domain.LikedTracksViewModel
import com.example.musicapp.ui.theme.Black80
import com.example.musicapp.ui.theme.Red60
import com.example.musicapp.ui.theme.White80

@SuppressLint("ServiceCast")
@Composable
fun PlayerScreen(
    likedTracksViewModel: LikedTracksViewModel,
    userId: String,
    track: Track,
    isPlaying: Boolean,
    currentPosition: Int,
    onPlayClick: () -> Unit,
    onTrackClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onArtistClick: (String) -> Unit,
    onBackClick: () -> Unit
) {

    val likedTracksState by likedTracksViewModel.likedTracksState.collectAsState()
    val duration = likedTracksViewModel.getDuration()


    var volume by remember { mutableFloatStateOf(0.5f) }

    VolumeObserver { newVolume ->
        volume = newVolume
    }


    val context = LocalContext.current
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager


    fun onVolumeChange(newVolume: Float) {
        volume = newVolume
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volumeIndex = (newVolume * maxVolume).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeIndex, 0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black80)
            .clickable { onTrackClick() }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = White80
                )
            }
        }


        Image(
            painter = rememberAsyncImagePainter(model = track.imageUrl),
            contentDescription = "Track Image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = track.title,
                        color = White80,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = track.artist,
                        color = White80.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable {
                            onArtistClick(track.artist_id.toString())
                        }
                    )
                }

                IconButton(
                    onClick = {
                        likedTracksViewModel.toggleLike(userId, track.id)
                    }
                ) {
                    Icon(
                        imageVector = if (likedTracksState.likedTrackIds.contains(track.id.toString()))
                            Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (likedTracksState.likedTrackIds.contains(track.id.toString()))
                            "Unlike" else "Like",
                        tint = if (likedTracksState.likedTrackIds.contains(track.id.toString())) Red60 else Color.Gray
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))


        Column {
            Slider(
                value = currentPosition.toFloat(),
                valueRange = 0f..duration.toFloat(),
                onValueChange = { newPosition ->
                    likedTracksViewModel.seekTo(newPosition.toInt())
                },
                modifier = Modifier.fillMaxWidth(0.95f).align(Alignment.CenterHorizontally),
                colors = SliderDefaults.colors(
                    thumbColor = Red60,
                    activeTrackColor = Red60
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition),
                    color = White80,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "-${formatTime(duration - currentPosition)}",
                    color = White80,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(horizontal = 32.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousClick) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Previous",
                    tint = White80
                )
            }
            IconButton(onClick = onPlayClick) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = White80
                )
            }
            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next",
                    tint = White80
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.VolumeDown,
                contentDescription = "Volume Down",
                tint = White80
            )
            Slider(
                value = volume,
                onValueChange = { newVolume -> onVolumeChange(newVolume) },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Red60,
                    activeTrackColor = Red60
                ),
                modifier = Modifier.weight(0.6f)
            )
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Volume Up",
                tint = White80
            )
        }
    }
}

private fun formatTime(milliseconds: Int): String {
    val minutes = milliseconds / 1000 / 60
    val seconds = (milliseconds / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}


