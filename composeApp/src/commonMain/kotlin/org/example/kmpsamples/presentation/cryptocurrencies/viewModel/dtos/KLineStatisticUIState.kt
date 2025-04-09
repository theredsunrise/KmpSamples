package org.example.kmpsamples.presentation.cryptocurrencies.viewModel.dtos

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import org.example.kmpsamples.domain.KlineStatistic
import org.example.kmpsamples.presentation.cryptocurrencies.views.ListItem

@Stable
@Immutable
class KLineStatisticUIState(item: KlineStatistic) : ListItem {
    val symbol: String = item.symbol
    val minValue: Float = item.minValue
    val maxValue: Float = item.maxValue
    override val id = symbol
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KLineStatisticUIState

        if (minValue != other.minValue) return false
        if (maxValue != other.maxValue) return false
        if (symbol != other.symbol) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minValue.hashCode()
        result = 31 * result + maxValue.hashCode()
        result = 31 * result + symbol.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}