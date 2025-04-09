package org.example.kmpsamples.presentation.cryptocurrencies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.button_start
import kmpsamples.composeapp.generated.resources.statistic
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.CryptocurrencyViewModel
import org.example.kmpsamples.presentation.cryptocurrencies.views.CryptocurrencyList
import org.example.kmpsamples.presentation.cryptocurrencies.views.KlineListItem
import org.example.kmpsamples.presentation.cryptocurrencies.views.KlineStatisticListItem
import org.example.kmpsamples.presentation.fillMaxSizeModifier
import org.example.kmpsamples.presentation.fillMaxWidthModifier
import org.example.kmpsamples.presentation.getWindowSizeClass
import org.jetbrains.compose.resources.stringResource

@Composable
fun CryptocurrencyScreen(
    modifier: Modifier,
    uiState: State<CryptocurrencyViewModel.UIState>,
    onStartCollecting: () -> Unit
) {
    val windowSizeClass = getWindowSizeClass()
    val shouldAddStatisticToRow = windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.requiredHeight(20.dp))
        Button(onClick = onStartCollecting, Modifier.wrapContentHeight()) {
            Text(stringResource(Res.string.button_start))
        }
        Spacer(Modifier.requiredHeight(20.dp))
        Row(fillMaxSizeModifier.weight(1f)) {
            val count =
                uiState.value.cryptoCurrencies.count() + (if (shouldAddStatisticToRow) 1 else 0)
            val rowListModifier = fillMaxWidthModifier.weight(1f / count).fillMaxHeight()
            uiState.value.cryptoCurrencies.forEach { listItemUIState ->
                CryptocurrencyList(
                    rowListModifier,
                    listItemUIState.uiData,
                    listItemUIState.symbol
                ) { listItem -> KlineListItem(fillMaxWidthModifier.wrapContentHeight(), listItem) }
            }
            if (shouldAddStatisticToRow) {
                statisticList(rowListModifier, uiState)
            }
        }
        Spacer(Modifier.requiredHeight(20.dp))
        if (!shouldAddStatisticToRow) {
            statisticList(Modifier.wrapContentHeight(), uiState)
        }
    }
}

@Composable
private fun statisticList(modifier: Modifier, uiState: State<CryptocurrencyViewModel.UIState>) {
    CryptocurrencyList(
        modifier,
        uiState.value.statistic,
        stringResource(Res.string.statistic).toUpperCase(Locale.current)
    ) { listItem ->
        KlineStatisticListItem(
            Modifier.fillMaxWidth(0.5f).wrapContentHeight(),
            listItem
        )
    }
}