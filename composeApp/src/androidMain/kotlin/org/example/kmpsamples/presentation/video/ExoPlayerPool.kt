package org.example.kmpsamples.presentation.video

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import org.example.kmpsamples.presentation.video.ExoPlayerPoolInterface.ExoPlayerItem

interface ExoPlayerPoolInterface {
    @OptIn(UnstableApi::class)
    class ExoPlayerItem(
        val id: Int,
        val exoPlayer: ExoPlayer
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as ExoPlayerItem
            return id == other.id
        }

        override fun hashCode(): Int {
            return id
        }

        fun load(url: String, listener: Player.Listener) {
            if (exoPlayer.isReleased) {
                println("***** load skipped!")
                return
            }
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.stop()
            exoPlayer.addListener(listener)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }

        fun play() {
            if (exoPlayer.isReleased || !isReady()) {
                println("***** play skipped! ${exoPlayer.isReleased}")
                return
            }
            if (!exoPlayer.isPlaying) {
                exoPlayer.play()
            }
        }

        @OptIn(UnstableApi::class)
        fun pause() {
            if (exoPlayer.isReleased || !isReady()) {
                println("***** pause skipped! ${exoPlayer.isReleased}")
                return
            }
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            }
        }

        fun isReady(): Boolean {
            if (exoPlayer.isReleased) {
                return false
            }
            return exoPlayer.playbackState == STATE_READY
        }

        fun isPLaying(): Boolean {
            if (exoPlayer.isReleased || !isReady()) {
                return false
            }
            return exoPlayer.isPlaying
        }

        fun clean(listener: Player.Listener?) {
            listener?.also {
                exoPlayer.removeListener(it)
            }
            if (exoPlayer.isReleased) {
                return
            }
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.clearVideoSurface()
        }

        fun release(listener: Player.Listener?) {
            clean(listener)
            if (!exoPlayer.isReleased) {
                exoPlayer.release()
            }
        }
    }

    fun give(id: Int)
    fun take(): ExoPlayerItem
    fun dispose()
}

class ExoPlayerPool(private val applicationContext: Context, capacity: Int = 10) :
    ExoPlayerPoolInterface {
    class ExoPlayerPoolItem(
        var available: Boolean = true,
        val exoPlayerItem: ExoPlayerItem
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ExoPlayerPoolItem

            if (available != other.available) return false
            if (exoPlayerItem != other.exoPlayerItem) return false

            return true
        }

        override fun hashCode(): Int {
            var result = available.hashCode()
            result = 31 * result + exoPlayerItem.hashCode()
            return result
        }
    }

    private val pool = MutableList(capacity) { index ->
        ExoPlayerPoolItem(
            true,
            exoPlayerItem = create(index)
        )
    }

    override fun take(): ExoPlayerItem {
        return pool.find { it.available }?.let { poolItem ->
            poolItem.available = false
            poolItem.exoPlayerItem
        } ?: create(pool.size - 1)
    }

    override fun give(id: Int) {
        pool.find { it.exoPlayerItem.id == id }?.also {
            it.available = true
        }
    }

    @OptIn(UnstableApi::class)
    private fun create(id: Int): ExoPlayerItem {
        return ExoPlayerItem(
            id,
            ExoPlayer.Builder(applicationContext)
                .setUseLazyPreparation(true)
                .build().also {
                    it.repeatMode = REPEAT_MODE_ALL
                }
        )
    }

    override fun dispose() {
        pool.forEach { item ->
            println("***** release ExoPlayer ${item.exoPlayerItem.id}")
            item.exoPlayerItem.release(null)
        }
        pool.clear()
    }
}