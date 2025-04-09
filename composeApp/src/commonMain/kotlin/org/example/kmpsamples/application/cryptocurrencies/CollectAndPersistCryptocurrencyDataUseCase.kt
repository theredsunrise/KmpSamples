package org.example.kmpsamples.application.cryptocurrencies

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.example.kmpsamples.application.interfaces.CryptocurrencyRepositoryInterface
import org.example.kmpsamples.application.interfaces.CryptocurrencyStorageRepositoryInterface
import org.example.kmpsamples.domain.KlineData
import org.example.kmpsamples.shared.checkIfNotMainThread

class CollectAndPersistCryptocurrencyDataUseCase(
    private val capacity: Int,
    private val cryptoCurrencyRepository: CryptocurrencyRepositoryInterface,
    val cryptoCurrencyStorageRepository: CryptocurrencyStorageRepositoryInterface,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    operator fun invoke(symbol: String): Flow<KlineData> {
        return cryptoCurrencyRepository.collect(symbol).map {
            checkIfNotMainThread()
            cryptoCurrencyStorageRepository.addWithCapacity(it, capacity).firstOrNull()
            it
        }.flowOn(dispatcher)
    }
}