package org.example.kmpsamples.application.interfaces

import kotlinx.coroutines.flow.Flow
import org.example.kmpsamples.domain.KlineData
import org.example.kmpsamples.domain.KlineStatistic

interface CryptocurrencyStorageRepositoryInterface {
    fun addWithCapacity(klineData: KlineData, capacity: Int): Flow<KlineData>
    fun getAll(symbol: String): Flow<List<KlineData>>
    fun getStatistic(): Flow<List<KlineStatistic>>
}