package org.example.kmpsamples.application.cryptocurrencies

import org.example.kmpsamples.domain.KlineData
import org.example.kmpsamples.shared.ResultState

data class KlineDataFromDbDto(val symbol: String, val data: ResultState<List<KlineData>>)
