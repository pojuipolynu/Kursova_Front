import android.media.MediaPlayer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlayerManager @Inject constructor() {
    private var mediaPlayer: MediaPlayer? = null

    // Play track and notify when it's complete
    fun play(trackUrl: String, onCompletion: () -> Unit) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setOnCompletionListener {
                    onCompletion()  // Notify that the track has finished playing
                }
            }
        } else {
            mediaPlayer?.reset()
        }

        mediaPlayer?.apply {
            setDataSource(trackUrl)
            prepare()
            start()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }
}

//class MediaPlayerManager @Inject constructor() {
//    private var mediaPlayer: MediaPlayer? = null
//
//    fun play(trackUrl: String, onCompletion: () -> Unit) {
//        if (mediaPlayer == null) {
//            mediaPlayer = MediaPlayer().apply {
//                setOnCompletionListener {
//                    onCompletion()
//                }
//            }
//        } else {
//            mediaPlayer?.reset()
//        }
//
//        mediaPlayer?.apply {
//            setDataSource(trackUrl)
//            prepare()
//            start()
//        }
//    }
//
//    fun pause() {
//        mediaPlayer?.pause()
//    }
//
//    fun resume() {
//        mediaPlayer?.start()
//    }
//
//    fun stop() {
//        mediaPlayer?.stop()
//        mediaPlayer?.release()
//        mediaPlayer = null
//    }
//
//    fun seekTo(position: Int) {
//        mediaPlayer?.seekTo(position)
//    }
//
//    fun getCurrentPosition(): Int {
//        return mediaPlayer?.currentPosition ?: 0
//    }
//
//    fun getDuration(): Int {
//        return mediaPlayer?.duration ?: 0
//    }
//}
