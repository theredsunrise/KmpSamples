package org.example.kmpsamples.infrastructure.data.room

import org.example.kmpsamples.domain.KlineData
import org.example.kmpsamples.domain.KlineStatistic
import org.example.kmpsamples.infrastructure.data.room.dtos.KlineEntityDto
import org.example.kmpsamples.infrastructure.data.room.dtos.KlineStatisticViewDto

interface RoomCryptocurrencyMapperInterface {
    fun fromDomain(klineData: KlineData): KlineEntityDto
    fun toDomain(klineEntityDto: KlineEntityDto): KlineData
    fun toDomain(klineStatisticViewDto: KlineStatisticViewDto): KlineStatistic
}

object RoomCryptocurrencyMapper : RoomCryptocurrencyMapperInterface {
    override fun fromDomain(klineData: KlineData): KlineEntityDto {
        return KlineEntityDto(
            klineData.id,
            klineData.symbol,
            klineData.eventTime,
            klineData.openPrice,
            klineData.closePrice,
            klineData.highPrice,
            klineData.lowPrice
        )
    }

    override fun toDomain(klineEntityDto: KlineEntityDto): KlineData {
        return with(klineEntityDto) {
            return KlineData(
                id,
                symbol,
                eventTime,
                openPrice,
                closePrice,
                highPrice,
                lowPrice
            )
        }
    }

    override fun toDomain(klineStatisticViewDto: KlineStatisticViewDto): KlineStatistic {
        return with(klineStatisticViewDto) {
            KlineStatistic(
                minValue,
                maxValue,
                symbol
            )
        }
    }
}