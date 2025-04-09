package org.example.kmpsamples.presentation.cryptocurrencies.viewModel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import org.example.kmpsamples.application.cryptocurrencies.CollectMultipleCryptocurrencyDataUseCase
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.CryptocurrencyViewModel.Actions.TrackCryptocurrencies
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.dtos.KLineDataFromDbUIState
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.dtos.KLineStatisticUIState
import org.example.kmpsamples.shared.ResultState
import org.example.kmpsamples.shared.ResultState.None
import org.example.kmpsamples.shared.asStateResult
import org.example.kmpsamples.shared.checkIfNotMainThread

class CryptocurrencyViewModel(
    private val useCase: CollectMultipleCryptocurrencyDataUseCase
) :
    ViewModel() {

    @Stable
    @Immutable
    data class UIState(
        val wasStarted: Boolean = false,
        val statistic: ResultState<List<KLineStatisticUIState>> = None,
        val cryptoCurrencies: List<KLineDataFromDbUIState> = emptyList(),
    )

    var onStartEmittingUiState: ((recentAction: Actions) -> Unit)? = null
    private var trigger = MutableSharedFlow<TrackCryptocurrencies>(1, 1, BufferOverflow.DROP_LATEST)

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
    val state: StateFlow<UIState> = trigger
        .flatMapLatest { action ->
            checkIfNotMainThread()
            combine(
                useCase.invoke(action.symbols)
                    .map { listOfDtos ->
                        listOfDtos.map { KLineDataFromDbUIState(it) }
                    },
                useCase.collectAndStoreUseCase.cryptoCurrencyStorageRepository.getStatistic()
                    .map { listOfDtos -> listOfDtos.map { dto -> KLineStatisticUIState(dto) } }
                    .asStateResult()
            ) { data, statistic ->
                checkIfNotMainThread()
                UIState(true, statistic, data)
            }
        }.onStart {
            trigger.firstOrNull()?.also {
                onStartEmittingUiState?.invoke(it)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UIState())

    sealed interface Actions {
        data class TrackCryptocurrencies(val symbols: List<String>) : Actions
    }

    fun doAction(action: Actions) {
        when (action) {
            is TrackCryptocurrencies -> trigger.tryEmit(action)
        }
    }
}