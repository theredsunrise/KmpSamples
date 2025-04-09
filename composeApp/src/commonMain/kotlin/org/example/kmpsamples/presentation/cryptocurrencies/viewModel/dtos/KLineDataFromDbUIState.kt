package org.example.kmpsamples.presentation.cryptocurrencies.viewModel.dtos

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import org.example.kmpsamples.application.cryptocurrencies.KlineDataFromDbDto
import org.example.kmpsamples.shared.ResultState
import org.example.kmpsamples.shared.transform

@Stable
@Immutable
class KLineDataFromDbUIState(item: KlineDataFromDbDto) {
    val symbol: String = item.symbol
    val uiData: ResultState<List<KLineDataUIState>> =
        item.data.transform { dto -> dto.map { KLineDataUIState(it) } }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KLineDataFromDbUIState

        if (symbol != other.symbol) return false
        if (uiData != other.uiData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = symbol.hashCode()
        result = 31 * result + uiData.hashCode()
        return result
    }
}