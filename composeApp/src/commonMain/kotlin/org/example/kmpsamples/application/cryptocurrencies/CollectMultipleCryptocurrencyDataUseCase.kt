package org.example.kmpsamples.application.cryptocurrencies

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.example.kmpsamples.shared.asStateResult
import org.example.kmpsamples.shared.checkIfNotMainThread

class CollectMultipleCryptocurrencyDataUseCase(
    val collectAndStoreUseCase: CollectAndPersistCryptocurrencyDataUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(symbols: List<String>): Flow<List<KlineDataFromDbDto>> {
        val flows = symbols.map { symbol ->
            collectAndStoreUseCase.invoke(symbol)
                .flatMapLatest {
                    checkIfNotMainThread()
                    collectAndStoreUseCase.cryptoCurrencyStorageRepository.getAll(symbol)
                }.asStateResult().map {
                    KlineDataFromDbDto(symbol, it)
                }
        }
        return combine(flows) { it.toList() }.flowOn(dispatcher)
    }
}