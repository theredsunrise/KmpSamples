package org.example.kmpsamples.presentation.deepLinks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DeepLinkViewModel : ViewModel() {

    sealed interface Event {
        data class ShowToast(val message: String) : Event
    }

    sealed interface Intent {
        data object Process : Intent
    }

    private val _events = Channel<Event>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    //    private val _events = MutableSharedFlow<Event>(3)
//    val events = _events.asSharedFlow()
    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.Process -> handleProcess()
        }
    }

    private fun sendEvent(message: String) {
        _events.trySend(Event.ShowToast(message))
        // _events.tryEmit(Event.ShowToast(message))
    }

    private fun handleProcess() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
            sendEvent("Hello World")
        }
    }
}