package org.example.kmpsamples.infrastructure.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import org.example.kmpsamples.infrastructure.data.room.dtos.KlineEntityDto

@Dao
interface KlineEntityDao {
    @Upsert
    suspend fun upsert(kline: KlineEntityDto): Long

    @Delete
    suspend fun delete(klineEntities: List<KlineEntityDto>): Int

    @Query("SELECT * FROM KLine WHERE SYMBOL=(:symbol) ORDER BY eventTime DESC")
    suspend fun getAll(symbol: String): List<KlineEntityDto>

    @Query("SELECT * FROM Kline WHERE SYMBOL=(:symbol) ORDER BY eventTime ASC LIMIT (:count)")
    suspend fun getOldest(symbol: String, count: Int): List<KlineEntityDto>

    @Query("SELECT COUNT(*) FROM Kline WHERE SYMBOL=(:symbol)")
    suspend fun getCount(symbol: String): Int

    @Transaction
    suspend fun replaceOldest(newKlineEntityDto: KlineEntityDto, capacity: Int): KlineEntityDto? {
        val diff = getCount(newKlineEntityDto.symbol) - (capacity - 1)
        if (diff > 0) {
            getOldest(newKlineEntityDto.symbol, diff).also {
                delete(it)
            }
        }
        upsert(newKlineEntityDto)
        return newKlineEntityDto
    }
}