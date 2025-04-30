package org.example.kmpsamples.presentation.video

import com.example.kmpsamples.ui.layerview.NSKeyValueObservingProtocol
import com.example.kmpsamples.ui.layerview.UILayerView
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.kmpsamples.presentation.video.VideoLooperState.ERROR
import org.example.kmpsamples.presentation.video.VideoLooperState.PAUSED
import org.example.kmpsamples.presentation.video.VideoLooperState.PLAYING
import org.example.kmpsamples.presentation.video.VideoLooperState.UNLOADED
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVPlayerLooper
import platform.AVFoundation.AVPlayerStatusFailed
import platform.AVFoundation.AVPlayerStatusReadyToPlay
import platform.AVFoundation.AVPlayerStatusUnknown
import platform.AVFoundation.AVPlayerTimeControlStatusPaused
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVQueuePlayer
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.timeControlStatus
import platform.Foundation.NSKeyValueChangeNewKey
import platform.Foundation.NSKeyValueObservingOptionInitial
import platform.Foundation.NSKeyValueObservingOptionNew
import platform.Foundation.NSURL
import platform.Foundation.addObserver
import platform.Foundation.removeObserver
import platform.darwin.NSObject

class IosVideoLooperController() {

    private var loopPlayer: AVPlayerLooper? = null
    private var queuePlayer: AVQueuePlayer? = null
    private var _state = MutableStateFlow<VideoLooperState>(UNLOADED)
    val state: StateFlow<VideoLooperState> = _state

    @OptIn(ExperimentalForeignApi::class)
    private val timeControlStatusObserver = object : NSObject(), NSKeyValueObservingProtocol {
        override fun observeValueForKeyPath(
            keyPath: String?,
            ofObject: Any?,
            change: Map<Any?, *>?,
            context: COpaquePointer?
        ) {
            change?.get(NSKeyValueChangeNewKey)?.also {
                when (it) {
                    AVPlayerTimeControlStatusPlaying -> _state.tryEmit(PLAYING)
                    AVPlayerTimeControlStatusPaused -> _state.tryEmit(PAUSED)
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private val playerStatusObserver = object : NSObject(), NSKeyValueObservingProtocol {
        override fun observeValueForKeyPath(
            keyPath: String?,
            ofObject: Any?,
            change: Map<Any?, *>?,
            context: COpaquePointer?
        ) {
            change?.get(NSKeyValueChangeNewKey)?.also {
                val item = ofObject as AVQueuePlayer
                when (it) {
                    AVPlayerStatusReadyToPlay -> _state.tryEmit(VideoLooperState.LOADED)
                    AVPlayerStatusFailed -> _state.value =
                        ERROR(item.error?.localizedFailureReason.orEmpty())

                    AVPlayerStatusUnknown -> _state.value = ERROR("Failed to load media.")
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun detachFromView(view: UILayerView) {
        view.layer.sublayers?.mapNotNull { it as? AVPlayerLayer }
            ?.forEach { it.removeFromSuperlayer() }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun attachToView(view: UILayerView) {
        detachFromView(view)
        queuePlayer ?: return
        val layer = AVPlayerLayer.playerLayerWithPlayer(queuePlayer)
        layer.videoGravity = AVLayerVideoGravityResizeAspectFill
        view.layer.addSublayer(layer)
    }

    @OptIn(ExperimentalForeignApi::class)
    fun load(url: String) {
        dispose()

        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl == null) {
            _state.tryEmit(ERROR("Invalid media url."))
            return
        }

        val queuePlayer = AVQueuePlayer()
        val item = AVPlayerItem(nsUrl)
        val loopPlayer = AVPlayerLooper.playerLooperWithPlayer(queuePlayer, item)

        println("Add observers")
        queuePlayer.addObserver(
            timeControlStatusObserver,
            "timeControlStatus",
            NSKeyValueObservingOptionNew,
            null
        )
        queuePlayer.addObserver(
            playerStatusObserver,
            "status",
            NSKeyValueObservingOptionNew or NSKeyValueObservingOptionInitial,
            null
        )
        this.loopPlayer = loopPlayer
        this.queuePlayer = queuePlayer
    }

    fun start() {
        println("Start")
        queuePlayer?.play()
    }

    fun isReady(): Boolean {
        return queuePlayer?.status == AVPlayerStatusReadyToPlay
    }

    fun isPlaying(): Boolean {
        return queuePlayer?.timeControlStatus == AVPlayerTimeControlStatusPlaying
    }

    fun pause() {
        if (isPlaying()) {
            println("Pause")
            queuePlayer?.pause()
        }
    }

    fun dispose() {
        queuePlayer?.apply {
            removeObserver(playerStatusObserver, "status")
            removeObserver(timeControlStatusObserver, "timeControlStatus")
            println("Remove observers")
        }
        pause()
        queuePlayer?.removeAllItems()
        queuePlayer = null
        loopPlayer = null
    }
}