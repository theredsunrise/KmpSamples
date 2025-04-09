package org.example.kmpsamples.infrastructure.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KlineRemoteDto(
    @SerialName("e") val eventType: String,   // Event type
    @SerialName("E") val eventTime: Long,     // Event time
    @SerialName("s") val symbol: String,      // Symbol
    @SerialName("k") val klineData: KlineRemoteData // Kline data
)

@Serializable
data class KlineRemoteData(
    @SerialName("t") val startTime: Long,       // Kline start time
    @SerialName("T") val closeTime: Long,       // Kline close time
    @SerialName("s") val symbol: String,        // Symbol
    @SerialName("i") val interval: String,      // Interval
    @SerialName("f") val firstTradeId: Long,    // First trade ID
    @SerialName("L") val lastTradeId: Long,     // Last trade ID
    @SerialName("o") val openPrice: String,     // Open price
    @SerialName("c") val closePrice: String,    // Close price
    @SerialName("h") val highPrice: String,     // High price
    @SerialName("l") val lowPrice: String,      // Low price
    @SerialName("v") val baseAssetVolume: String, // Base asset volume
    @SerialName("n") val numberOfTrades: Int,   // Number of trades
    @SerialName("x") val isClosed: Boolean,     // Is this kline closed?
    @SerialName("q") val quoteAssetVolume: String, // Quote asset volume
    @SerialName("V") val takerBuyBaseVolume: String, // Taker buy base asset volume
    @SerialName("Q") val takerBuyQuoteVolume: String, // Taker buy quote asset volume
    @SerialName("B") val ignore: String         // Ignore
)