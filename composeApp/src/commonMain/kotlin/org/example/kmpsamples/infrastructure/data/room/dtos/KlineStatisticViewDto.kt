package org.example.kmpsamples.infrastructure.data.room.dtos

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "KlineStatisticView",
    value = "SELECT MIN(closePrice) as minValue, MAX(closePrice) as maxValue, symbol as symbol from Kline GROUP BY symbol ORDER BY symbol ASC"
)
data class KlineStatisticViewDto(
    val minValue: Float, val maxValue: Float, val symbol: String
)