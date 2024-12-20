package com.example.musicapp.features.auth.presentation

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.musicapp.R
import com.example.musicapp.components.HeaderComponent
import com.example.musicapp.features.auth.data.AuthResult
import com.example.musicapp.features.auth.domain.AuthViewModel
import com.example.musicapp.ui.theme.Black90
import com.example.musicapp.ui.theme.Red60
import com.example.musicapp.ui.theme.White80


@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    activity: Activity,
    onLoginSuccess: () -> Unit = {}
) {
    val authResult by authViewModel.authResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Black90)
            .padding(horizontal = 16.dp)
    ) {
        HeaderComponent(text = "Велкам)))")

        Spacer(modifier = Modifier.size(24.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val result = authResult) {
                is AuthResult.Idle -> {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Red60,
                            contentColor = Red60
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { authViewModel.signInWithGoogle(activity) }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .background(color = Red60),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ввійти через Google",
                                style = MaterialTheme.typography.bodyLarge,
                                color = White80,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Image(
                                painter = painterResource(R.drawable.play_icon),
                                contentDescription = null,
                                Modifier.size(18.dp)
                            )
                        }
                    }
                }
                is AuthResult.Loading -> {
                    Text("Loading...", color = White80)
                }
                is AuthResult.Success -> {
                    onLoginSuccess()
                }
                is AuthResult.Error -> {
                    Column {
                        Button(onClick = { authViewModel.signInWithGoogle(activity) }) {
                            Text("Retry Sign in")
                        }
                        Text(
                            text = "Error: ${result.message}",
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }
}
