package org.example.kmpsamples.domain

data class KlineData(
    val id: Long,
    val symbol: String,
    val eventTime: Long,
    val openPrice: Float,
    val closePrice: Float,
    val highPrice: Float,
    val lowPrice: Float
) : EntityInterface