package org.example.kmpsamples.application.interfaces

import kotlinx.coroutines.flow.Flow
import org.example.kmpsamples.domain.KlineData

interface CryptocurrencyRepositoryInterface {
    sealed class Exceptions(message: String) : Exception(message) {
        data class SocketException(val reason: String) :
            Exceptions("Unable to create the WebSocket: $reason")

        data class CollectingDataException(val reason: String) :
            Exceptions("Unable to collect data from the WebSocket: $reason")

        data class ClosedStreamException(val reason: String) :
            Exceptions("Closed stream with reason: $reason")
    }

    fun collect(symbol: String): Flow<KlineData>
}