package org.example.kmpsamples.presentation.cryptocurrencies.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.crypto_high_price
import kmpsamples.composeapp.generated.resources.crypto_low_price
import kmpsamples.composeapp.generated.resources.crypto_time
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.dtos.KLineDataUIState
import org.example.kmpsamples.presentation.fillMaxWidthModifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun KlineListItem(modifier: Modifier, klineData: KLineDataUIState) {
    Column(modifier) {
        Text(
            stringResource(Res.string.crypto_time, klineData.localDateTime),
            fillMaxWidthModifier,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            stringResource(Res.string.crypto_low_price, klineData.lowPrice),
            fillMaxWidthModifier,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            stringResource(Res.string.crypto_high_price, klineData.highPrice),
            fillMaxWidthModifier,
            style = MaterialTheme.typography.bodySmall
        )
    }
}