package org.example.kmpsamples.infrastructure.network

import org.example.kmpsamples.domain.KlineData

interface CryptocurrencyRemoteMapperInterface {
    fun toDomain(klineRemoteDto: KlineRemoteDto): KlineData
}

object CryptocurrencyRemoteMapper : CryptocurrencyRemoteMapperInterface {

    override fun toDomain(klineRemoteDto: KlineRemoteDto): KlineData {
        return KlineData(
            klineRemoteDto.eventTime,
            klineRemoteDto.symbol,
            klineRemoteDto.eventTime,
            klineRemoteDto.klineData.openPrice.toFloat(),
            klineRemoteDto.klineData.closePrice.toFloat(),
            klineRemoteDto.klineData.highPrice.toFloat(),
            klineRemoteDto.klineData.lowPrice.toFloat()
        )
    }
}