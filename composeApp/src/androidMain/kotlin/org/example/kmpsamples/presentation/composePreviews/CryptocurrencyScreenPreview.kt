package org.example.kmpsamples.presentation.composePreviews

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import org.example.kmpsamples.presentation.cryptocurrencies.CryptocurrencyScreen
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.CryptocurrencyViewModel
import org.example.kmpsamples.presentation.fillMaxSizeModifier

@Preview
@Composable
private fun CryptocurrencyScreenPreview() {
    CustomMaterialTheme() {
        Surface(fillMaxSizeModifier.safeDrawingPadding()) {
            val uiState = remember { mutableStateOf(CryptocurrencyViewModel.UIState()) }
            CryptocurrencyScreen(fillMaxSizeModifier, uiState) {}
        }
    }
}