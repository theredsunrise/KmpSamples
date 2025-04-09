package org.example.kmpsamples.domain

data class KlineStatistic(val minValue: Float, val maxValue: Float, val symbol: String) :
    ValueObjectInterface
