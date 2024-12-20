package com.example.musicapp.features.main.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.main.profile.data.ProfileResult
import com.example.musicapp.features.main.profile.domain.ProfileViewModel
import com.example.musicapp.ui.theme.Black80
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.White80

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }

    val profileState by profileViewModel.profileState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Black90)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderComponent(text = "Ваш профіль")

        Spacer(modifier = Modifier.size(24.dp))

        when (val result = profileState) {
            is ProfileResult.Loading -> {
                CircularProgressIndicator(color = White80)
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
                    val initials = result.userProfile.user.name
                        .split(" ")
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
                    text = result.userProfile.user.name,
                    fontSize = 24.sp,
                    color = White80,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = result.userProfile.user.email,
                    fontSize = 16.sp,
                    color = White80,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.size(24.dp))

                PlaylistsSection(playlists = result.userProfile.playlists.map { it.title })
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        /// todo playlists
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
