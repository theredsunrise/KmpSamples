package org.example.kmpsamples.presentation.cryptocurrencies.viewModel.dtos

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import org.example.kmpsamples.domain.KlineData
import org.example.kmpsamples.presentation.cryptocurrencies.views.ListItem
import org.example.kmpsamples.shared.format
import org.example.kmpsamples.shared.millisToLocalDate

@Stable
@Immutable
class KLineDataUIState(item: KlineData) : ListItem {
    override val id = item.id.toString()
    val highPrice: Float = item.highPrice
    val lowPrice: Float = item.lowPrice
    val localDateTime: String = millisToLocalDate(item.eventTime).format()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KLineDataUIState

        if (highPrice != other.highPrice) return false
        if (lowPrice != other.lowPrice) return false
        if (id != other.id) return false
        if (localDateTime != other.localDateTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = highPrice.hashCode()
        result = 31 * result + lowPrice.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + localDateTime.hashCode()
        return result
    }
}