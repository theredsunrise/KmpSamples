package org.example.kmpsamples.presentation.deepLinks

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.receiveAsFlow

object IntentHandler {
    private val _dataState = Channel<String>(BUFFERED)
    val dataState = _dataState.receiveAsFlow()

    fun emitData(uri: String) {
        _dataState.trySend(uri)
    }
}