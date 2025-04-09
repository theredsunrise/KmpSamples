package org.example.kmpsamples.infrastructure.data.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.example.kmpsamples.application.interfaces.CryptocurrencyStorageRepositoryInterface
import org.example.kmpsamples.domain.KlineData
import org.example.kmpsamples.domain.KlineStatistic
import org.example.kmpsamples.infrastructure.data.room.dao.KlineEntityDao
import org.example.kmpsamples.infrastructure.data.room.dao.KlineStatisticViewDao

class RoomCryptocurrencyStorageRepository(
    private val klineEntityDao: KlineEntityDao,
    private val klineStatisticViewDao: KlineStatisticViewDao,
    private val mapper: RoomCryptocurrencyMapperInterface
) : CryptocurrencyStorageRepositoryInterface {

    override fun addWithCapacity(klineData: KlineData, capacity: Int): Flow<KlineData> = flow {
        val newEntity = mapper.fromDomain(klineData)
        klineEntityDao.replaceOldest(newEntity, capacity)
        emit(mapper.toDomain(newEntity))
    }

    override fun getAll(symbol: String): Flow<List<KlineData>> = flow {
        val domainObjects = klineEntityDao.getAll(symbol).map { mapper.toDomain(it) }
        emit(domainObjects)
    }

    override fun getStatistic(): Flow<List<KlineStatistic>> {
        return klineStatisticViewDao.getStatistic().map {
            it.map { dto -> mapper.toDomain(dto) }
        }
    }
}