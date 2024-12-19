package com.example.musicapp.features.main.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicapp.features.main.profile.data.ProfileResult
import com.example.musicapp.features.main.profile.domain.ProfileViewModel
import com.example.musicapp.ui.theme.White80

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }

    val profileState by profileViewModel.profileState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val result = profileState) {
            is ProfileResult.Loading -> {
                CircularProgressIndicator()
            }
            is ProfileResult.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = result.userProfile.user.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = White80
                    )
                    Text(
                        text = result.userProfile.user.email,
                        fontSize = 16.sp,
                        color = White80
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PlaylistsSection(playlists = result.userProfile.playlists.map { it.title })
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
fun PlaylistsSection(playlists: List<String>) {
    Column {
        playlists.forEach { playlist ->
            Text(text = playlist, fontSize = 18.sp)
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}