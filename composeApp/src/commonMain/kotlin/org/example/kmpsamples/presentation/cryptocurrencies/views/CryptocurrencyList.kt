package org.example.kmpsamples.presentation.cryptocurrencies.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.error_reason
import org.example.kmpsamples.presentation.fillMaxWidthModifier
import org.example.kmpsamples.shared.ResultState
import org.example.kmpsamples.shared.safeMessage
import org.jetbrains.compose.resources.stringResource

interface ListItem {
    val id: String
}

@Composable
fun <T : ListItem> CryptocurrencyList(
    modifier: Modifier,
    uiState: ResultState<List<T>>,
    title: String? = null,
    itemFactory: @Composable (listItem: T) -> Unit
) {
    when (uiState) {
        is ResultState.Success -> {
            Column(
                modifier
            ) {
                title?.also {
                    Text(
                        it.toUpperCase(Locale.current),
                        fillMaxWidthModifier,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.requiredHeight(5.dp))
                }
                LazyColumn(
                    fillMaxWidthModifier.wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(uiState.value, key = { it.id }) {
                        itemFactory(it)
                        HorizontalDivider(thickness = 1.dp)
                    }
                }
            }
        }

        is ResultState.None -> Unit

        else -> {
            Box(modifier) {
                when (uiState) {
                    is ResultState.Progress -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is ResultState.Failure -> Text(
                        stringResource(
                            Res.string.error_reason,
                            uiState.exception.safeMessage()
                        ),
                        Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )

                    else -> Unit
                }
            }
        }
    }
}