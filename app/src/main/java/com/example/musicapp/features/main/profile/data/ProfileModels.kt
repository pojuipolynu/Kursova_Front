package com.example.musicapp.features.main.profile.data

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class User(val id: String, val name: String, val email: String)
data class Playlist(val title: String, val user_id: String, val id: String)
