package com.example.musicapp.features.main.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.auth.domain.AuthViewModel
import com.example.musicapp.features.main.profile.data.Playlist
import com.example.musicapp.features.main.profile.data.ProfileResult
import com.example.musicapp.features.main.profile.domain.ProfileViewModel
import com.example.musicapp.ui.theme.Black80
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.White80
import com.example.musicapp.ui.theme.Red60

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToCreatePlaylist: () -> Unit,
    onNavigateToViewPlaylist: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    val profileState by profileViewModel.profileState.collectAsState()
    val currentUser = authViewModel.getCurrentUser()

    val userName = currentUser?.username ?: "User123"
    val userEmail = currentUser?.email ?: "user@gmail.com"
    val userId = currentUser?.userId ?: "1"

    var userStatus by remember { mutableStateOf<String?>(null) }

    val playlists by profileViewModel.playlists.collectAsState()

    LaunchedEffect(userId) {
        try {
            userStatus = profileViewModel.getUserStatus(userId)
        } catch (e: Exception) {
            userStatus = "FREE"
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Black90)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            HeaderComponent(text = "Ваш профіль")

            IconButton(
                onClick = { onLogout() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = White80
                )
            }
        }

        Spacer(modifier = Modifier.size(24.dp))

        when (val result = profileState) {
            is ProfileResult.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = White80
                )
            }

            is ProfileResult.Success -> {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            color = Black80,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = userName.split(" ")
                        .take(2)
                        .joinToString("") { it.first().uppercase() }

                    Text(
                        text = initials,
                        color = White80,
                        fontSize = 44.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.size(20.dp))

                Text(
                    text = userName,
                    fontSize = 24.sp,
                    color = White80,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = userEmail,
                    fontSize = 16.sp,
                    color = White80,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.size(16.dp))

                if (userStatus != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Платний тариф",
                            color = White80,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )

                        Switch(
                            checked = userStatus == "PAID",
                            onCheckedChange = { newStatus ->
                                userStatus = if (newStatus) "PAID" else "FREE"
                                profileViewModel.changeUserStatus(userId)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = White80,
                                uncheckedThumbColor = White80,
                                checkedTrackColor = Red60,
                                uncheckedTrackColor = Black80
                            ),
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(16.dp))

                    if (userStatus == "PAID") {
                        LaunchedEffect(userId) {
                            profileViewModel.fetchUserPlaylists(userId)
                        }

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            maxItemsInEachRow = 2,
                        ) {
                            CreatePlaylistButton(onClick = onNavigateToCreatePlaylist)

                            playlists.forEach { playlist ->
                                PlaylistItem(
                                    playlist = playlist,
                                    onClick = { onNavigateToViewPlaylist(playlist.id) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(24.dp))
                }
            }

            is ProfileResult.Error -> {
                Text(
                    text = "Error: ${result.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun CreatePlaylistButton(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth(0.46f)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(1f)
                .aspectRatio(1f)
                .border(1.dp, White80, RoundedCornerShape(8.dp))
                .background(Color.Transparent, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp, 40.dp)
                    .background(Red60, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Playlist",
                    tint = White80,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = "Додати плейліст",
            style = MaterialTheme.typography.bodySmall,
            color = White80,
            textAlign = TextAlign.Center
        )
    }
}



@Composable
fun PlaylistItem(
    playlist: Playlist, onClick: () -> Unit)
{
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth(0.46f)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(1f)
                .aspectRatio(1f)
                .background(color = Red60, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = playlist.title.firstOrNull()?.uppercase() ?: "",
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = playlist.title,
            style = MaterialTheme.typography.bodySmall,
            color = White80,
            textAlign = TextAlign.Left
        )
    }
}




