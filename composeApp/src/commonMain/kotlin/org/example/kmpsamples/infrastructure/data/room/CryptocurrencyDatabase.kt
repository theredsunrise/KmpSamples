package org.example.kmpsamples.infrastructure.data.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import org.example.kmpsamples.infrastructure.data.room.dao.KlineEntityDao
import org.example.kmpsamples.infrastructure.data.room.dao.KlineStatisticViewDao
import org.example.kmpsamples.infrastructure.data.room.dtos.KlineEntityDto
import org.example.kmpsamples.infrastructure.data.room.dtos.KlineStatisticViewDto

@Database(
    entities = [KlineEntityDto::class],
    views = [KlineStatisticViewDto::class],
    version = 1
)
@ConstructedBy(CryptocurrencyDatabaseConstructor::class)
abstract class CryptocurrencyDatabase : RoomDatabase() {
    abstract fun klineEntityDao(): KlineEntityDao
    abstract fun klineStatisticViewDao(): KlineStatisticViewDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object CryptocurrencyDatabaseConstructor : RoomDatabaseConstructor<CryptocurrencyDatabase> {
    override fun initialize(): CryptocurrencyDatabase
}