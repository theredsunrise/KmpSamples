package org.example.kmpsamples.presentation.cryptocurrencies.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.crypto_currency
import kmpsamples.composeapp.generated.resources.crypto_maximum_price
import kmpsamples.composeapp.generated.resources.crypto_minimum_price
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.dtos.KLineStatisticUIState
import org.example.kmpsamples.presentation.fillMaxWidthModifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun KlineStatisticListItem(modifier: Modifier, klineStatistic: KLineStatisticUIState) {
    Column(modifier) {
        Text(
            stringResource(Res.string.crypto_currency, klineStatistic.symbol),
            fillMaxWidthModifier,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            stringResource(Res.string.crypto_minimum_price, klineStatistic.minValue.toString()),
            fillMaxWidthModifier,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            stringResource(Res.string.crypto_maximum_price, klineStatistic.maxValue.toString()),
            fillMaxWidthModifier,
            style = MaterialTheme.typography.bodySmall
        )
    }
}