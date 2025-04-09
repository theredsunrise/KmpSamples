package org.example.kmpsamples.infrastructure.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.PINGER_DISABLED
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import org.example.kmpsamples.application.interfaces.CryptocurrencyRepositoryInterface
import org.example.kmpsamples.application.interfaces.CryptocurrencyRepositoryInterface.Exceptions.ClosedStreamException
import org.example.kmpsamples.application.interfaces.CryptocurrencyRepositoryInterface.Exceptions.CollectingDataException
import org.example.kmpsamples.domain.KlineData
import org.example.kmpsamples.shared.safeMessage

expect fun engine(): HttpClientEngine

class CryptocurrencyRepository(private val mapper: CryptocurrencyRemoteMapperInterface) :
    CryptocurrencyRepositoryInterface {

    private val client = HttpClient(engine()) {
        install(WebSockets) {
            pingIntervalMillis = PINGER_DISABLED
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10_000 // Set the request timeout (in ms)
            connectTimeoutMillis = 5_000 // Set the connection timeout (in ms)
            socketTimeoutMillis = 10_000 // Set the socket timeout (in ms)
        }
    }

    companion object {
        private const val BASE_URL = "wss://stream.binance.com:9443/ws/"
        fun createUrl(symbol: String): String {
            return "$BASE_URL$symbol@kline_1m"
        }
    }

    override fun collect(symbol: String) = flow {
        val session: DefaultClientWebSocketSession =
            try {
                client.webSocketSession(createUrl(symbol))
            } catch (e: Exception) {
                throw CryptocurrencyRepositoryInterface.Exceptions.SocketException(e.safeMessage())
            }

        val customReason: Exception? =
            try {
                collectData(session)
            } catch (e: Exception) {
                CollectingDataException(e.safeMessage())
            } finally {
                println("***** Socket session closed")
                session.close()
            }

        customReason?.also { throw it }
    }

    private suspend fun FlowCollector<KlineData>.collectData(
        session: DefaultClientWebSocketSession
    ): Exception? {
        for (frame in session.incoming) {
            if (!session.isActive) break
            when (frame) {
                is Frame.Text -> {
                    val data = session.receiveDeserialized<KlineRemoteDto>()
                    emit(mapper.toDomain(data))
                }

                is Frame.Close -> {
                    return ClosedStreamException(
                        frame.readReason().toString()
                    )
                }

                else -> {}
            }
        }
        return null
    }
}