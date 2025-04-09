package org.example.kmpsamples.infrastructure.data.room.dtos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Kline")
data class KlineEntityDto(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    @ColumnInfo(index = true, collate = ColumnInfo.NOCASE)
    val symbol: String,
    @ColumnInfo(index = true)
    val eventTime: Long,
    val openPrice: Float,
    val closePrice: Float,
    @ColumnInfo(index = true)
    val highPrice: Float,
    @ColumnInfo(index = true)
    val lowPrice: Float
)