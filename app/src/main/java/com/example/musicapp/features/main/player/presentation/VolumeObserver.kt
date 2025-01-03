import android.content.Context
import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun VolumeObserver(
    onVolumeChanged: (Float) -> Unit
) {
    val context = LocalContext.current

    // Створення обробника гучності через AudioManager
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()

    // Отримання поточного рівня гучності
    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxVolume

    // Викликаємо onVolumeChanged щоб оновити значення гучності
    onVolumeChanged(currentVolume)
}



