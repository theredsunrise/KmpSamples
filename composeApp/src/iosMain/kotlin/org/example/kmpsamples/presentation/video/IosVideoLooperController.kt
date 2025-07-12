package org.example.kmpsamples.presentation.video

import com.example.kmpsamples.ui.layerview.NSKeyValueObservingProtocol
import com.example.kmpsamples.ui.layerview.UILayerView
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.kmpsamples.presentation.video.VideoLooperState.ERROR
import org.example.kmpsamples.presentation.video.VideoLooperState.LOADED
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
import platform.Foundation.NSNumber
import platform.Foundation.NSThread
import platform.Foundation.NSURL
import platform.Foundation.addObserver
import platform.Foundation.removeObserver
import platform.darwin.NSObject

class IosVideoLooperController() {

    private var loopPlayer: AVPlayerLooper? = null
    private var queuePlayer: AVQueuePlayer? = null
    private var playerLayer: AVPlayerLayer? = null
    private var _state = MutableStateFlow<VideoLooperState>(UNLOADED)
    val state: StateFlow<VideoLooperState> = _state

    private val READY_FOR_DISPLAY_OBSERVER_NAME = "readyForDisplay"
    private val TIME_CONTROL_STATUS_OVERVER_NAME = "timeControlStatus"
    private val PLAYER_STATUS_OBSERVER_NAME = "status"

    @OptIn(ExperimentalForeignApi::class)
    private val readyToDisplayObserver = object : NSObject(), NSKeyValueObservingProtocol {
        override fun observeValueForKeyPath(
            keyPath: String?, ofObject: Any?, change: Map<Any?, *>?, context: COpaquePointer?
        ) {
            check(NSThread.isMainThread)
            change?.get(NSKeyValueChangeNewKey).let { it as? NSNumber }?.also {
                if (it.boolValue) {
                    _state.tryEmit(LOADED)
                } else {
                    _state.tryEmit(UNLOADED)
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private val timeControlStatusObserver = object : NSObject(), NSKeyValueObservingProtocol {
        override fun observeValueForKeyPath(
            keyPath: String?, ofObject: Any?, change: Map<Any?, *>?, context: COpaquePointer?
        ) {
            check(NSThread.isMainThread)
            if (playerLayer?.readyForDisplay == false) return;
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
            keyPath: String?, ofObject: Any?, change: Map<Any?, *>?, context: COpaquePointer?
        ) {
            check(NSThread.isMainThread)
            change?.get(NSKeyValueChangeNewKey)?.also {
                val item = ofObject as AVQueuePlayer
                when (it) {
                    AVPlayerStatusFailed -> _state.value =
                        ERROR(item.error?.localizedFailureReason.orEmpty())

                    AVPlayerStatusUnknown -> _state.tryEmit(UNLOADED)
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun detachFromView(view: UILayerView, id: Int): Boolean {
        check(NSThread.isMainThread)
        println("***** Detach from view: $id")
        val state: Int = view.layer.sublayers?.mapNotNull { it as? AVPlayerLayer }?.sumOf {
            println("**** Remove layer observer: $id")
            it.removeObserver(readyToDisplayObserver, READY_FOR_DISPLAY_OBSERVER_NAME)
            it.removeFromSuperlayer()
            1.toInt()
        } ?: 0
        this.playerLayer = null
        return state != 0
    }

    @OptIn(ExperimentalForeignApi::class)
    fun attachToView(view: UILayerView, id: Int): Boolean {
        check(NSThread.isMainThread)
        queuePlayer ?: return false
        detachFromView(view, id)
        println("**** Attach to view: $id")

        val layer = AVPlayerLayer.playerLayerWithPlayer(queuePlayer)
        println("**** Add layer observer: $id")
        layer.addObserver(
            readyToDisplayObserver,
            READY_FOR_DISPLAY_OBSERVER_NAME,
            NSKeyValueObservingOptionNew,
            null
        )
        layer.videoGravity = AVLayerVideoGravityResizeAspectFill
        view.layer.addSublayer(layer)
        view.setBounds(view.bounds) //resize layer
        this.playerLayer = layer
        return true
    }

    @OptIn(ExperimentalForeignApi::class)
    fun load(url: String, id: Int): Boolean {
        dispose(id)
        println("**** Load item: $id")

        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl == null) {
            _state.tryEmit(ERROR("Invalid media url."))
            return false
        }

        val queuePlayer = AVQueuePlayer()
        val item = AVPlayerItem(nsUrl)
        val loopPlayer = AVPlayerLooper.playerLooperWithPlayer(queuePlayer, item)

        println("**** Add observers")
        queuePlayer.addObserver(
            timeControlStatusObserver,
            TIME_CONTROL_STATUS_OVERVER_NAME,
            NSKeyValueObservingOptionNew,
            null
        )
        queuePlayer.addObserver(
            playerStatusObserver,
            PLAYER_STATUS_OBSERVER_NAME,
            NSKeyValueObservingOptionNew or NSKeyValueObservingOptionInitial,
            null
        )
        this.loopPlayer = loopPlayer
        this.queuePlayer = queuePlayer
        return true
    }

    fun play() {
        if (!isPlaying() && isReady()) {
            println("**** Play")
            queuePlayer?.play()
        }
    }

    fun isReady(): Boolean {
        val status =
            queuePlayer?.status == AVPlayerStatusReadyToPlay && playerLayer?.readyForDisplay == true
        println("**** IsReady $status")
        return status
    }

    fun isPlaying(): Boolean {
        val status = queuePlayer?.timeControlStatus == AVPlayerTimeControlStatusPlaying
        println("**** IsPlaying $status")
        return status
    }

    fun pause() {
        if (isPlaying() && isReady()) {
            println("**** Pause")
            queuePlayer?.pause()
        }
    }

    fun dispose(id: Int) {
        println("**** Dispose item: $id")
        queuePlayer?.apply {
            removeObserver(playerStatusObserver, PLAYER_STATUS_OBSERVER_NAME)
            removeObserver(timeControlStatusObserver, TIME_CONTROL_STATUS_OVERVER_NAME)
            println("**** Remove other observers: $id")
        }
        pause()
        queuePlayer?.removeAllItems()
        queuePlayer = null
        loopPlayer = null
    }
}
