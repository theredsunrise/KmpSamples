package org.example.kmpsamples.presentation.pickers.viewModel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.kmpsamples.presentation.pickers.PlatformImage

class GalleryPickerViewModel : ViewModel() {

    @Stable
    data class UIState(val avatar: PlatformImage?) {
        companion object {
            val default = UIState(null)
        }
    }

    sealed interface Intents {
        data class UpdateAvatar(val image: PlatformImage?) : Intents
    }

    private val _state = MutableStateFlow(UIState.default)
    val state = _state.asStateFlow()

    fun sendIntent(intent: Intents) {
        when (intent) {
            is Intents.UpdateAvatar -> reduceAvatar(intent.image)
        }
    }

    override fun onCleared() {
        reduceAvatar(null)
        super.onCleared()
    }

    private fun reduceAvatar(newAvatar: PlatformImage?) {
        val avatar = _state.value.avatar
        if (avatar === newAvatar) return
        _state.update {
            val copy = it.copy(avatar = newAvatar)
            copy
        }
        avatar?.recycle()
    }
}