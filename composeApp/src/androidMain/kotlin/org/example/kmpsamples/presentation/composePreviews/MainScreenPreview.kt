package org.example.kmpsamples.presentation.composePreviews

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import org.example.kmpsamples.presentation.MenuScreen
import org.example.kmpsamples.presentation.fillMaxSizeModifier

@Preview
@Composable
private fun MainScreenPreview() {
    CustomMaterialTheme() {
        Surface(fillMaxSizeModifier.safeDrawingPadding()) {
            MenuScreen(fillMaxSizeModifier, {}, {})
        }
    }
}