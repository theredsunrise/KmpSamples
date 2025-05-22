package org.example.kmpsamples.presentation.video

import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.kmpsamples.presentation.video.ExoPlayerPoolInterface.ExoPlayerItem
import org.example.kmpsamples.presentation.video.VideoLooperState.LOADED
import org.example.kmpsamples.presentation.video.VideoLooperState.PAUSED
import org.example.kmpsamples.presentation.video.VideoLooperState.PLAYING
import org.example.kmpsamples.presentation.video.VideoLooperState.UNLOADED

class AndroidVideoLooperController(
    private val pool: ExoPlayerPoolInterface
) : Player.Listener {
    private var exoPlayerItem: ExoPlayerItem? = null
    private var _state = MutableStateFlow<VideoLooperState>(UNLOADED)
    val state: StateFlow<VideoLooperState> = _state

    override fun onPlayerError(error: PlaybackException) {
        println("***** onPlayerError $error")
        _state.tryEmit(VideoLooperState.ERROR(error.localizedMessage ?: error.message.orEmpty()))
    }

    override fun onPlaybackStateChanged(state: Int) {
        exoPlayerItem?.exoPlayer?.also {
            if (it.playerError != null) {
                it.clearMediaItems()
                return
            }
        }

        println("***** onPlaybackStateChanged $state")
        when (state) {
            STATE_READY -> _state.tryEmit(LOADED)
            STATE_ENDED -> _state.tryEmit(PAUSED)
            STATE_IDLE -> _state.tryEmit(UNLOADED)
            STATE_BUFFERING -> _state.tryEmit(UNLOADED)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        println("***** onIsPlayingChanged  $isPlaying")
        if (isPlaying) {
            _state.tryEmit(PLAYING)
        } else {
            _state.tryEmit(PAUSED)
        }
    }

    fun detachFromView(view: PlayerView): Boolean {
        if (view.player == null) {
            return false
        }
        println("***** detachFromView")
        pause()
        view.player = null
        return true
    }

    fun attachToView(view: PlayerView): Boolean {
        if (exoPlayerItem != null) {
            return false
        }
        println("***** attachToView")
        val exoPlayerItem = pool.take()
        view.player = exoPlayerItem.exoPlayer
        this@AndroidVideoLooperController.exoPlayerItem = exoPlayerItem
        return true
    }

    @OptIn(UnstableApi::class)
    fun load(url: String) {
        println("***** load")
        exoPlayerItem?.load(url, this@AndroidVideoLooperController)
    }

    fun play() {
        if (!isPlaying() && isReady()) {
            println("***** Start")
            exoPlayerItem?.play()
        }
    }

    fun isReady(): Boolean {
        val status = exoPlayerItem?.isReady() == true
        println("***** isReady $status")
        return status
    }

    fun isPlaying(): Boolean {
        val status = exoPlayerItem?.isPLaying() == true
        println("***** isPlaying $status")
        return status
    }

    fun pause() {
        if (isPlaying() && isReady()) {
            println("***** pause")
            exoPlayerItem?.pause()
        }
    }

    fun clean() {
        exoPlayerItem?.also {
            println("***** clean")
            it.clean(this)
            pool.give(it.id)
        }
        exoPlayerItem = null
    }
}