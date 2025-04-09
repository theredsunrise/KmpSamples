package org.example.kmpsamples.application.cryptocurrencies

import org.example.kmpsamples.domain.KlineData
import org.example.kmpsamples.shared.ResultState

object KlineDataFromDbMapper {
    fun fromDomain(symbol: String, klineData: ResultState<List<KlineData>>): KlineDataFromDbDto {
        return KlineDataFromDbDto(symbol, klineData)
    }
}