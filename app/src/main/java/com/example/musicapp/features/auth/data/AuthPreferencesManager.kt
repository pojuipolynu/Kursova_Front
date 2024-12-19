package com.example.musicapp.features.auth.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "AuthPreferences",
        Context.MODE_PRIVATE
    )

    fun saveUserId(userId: String) {
        prefs.edit {
            putString(KEY_USER_ID, userId)
        }
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun clearUserId() {
        prefs.edit {
            remove(KEY_USER_ID)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return getUserId() != null
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
    }
}