package org.example.kmpsamples.presentation.composePreviews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import org.example.kmpsamples.presentation.views.CustomDialog

@Preview
@Composable
private fun PreviewCustomDialog() {
    CustomMaterialTheme {
        CustomDialog(
            remember { mutableStateOf(true) },
            "Info",
            "Confirm",
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry." +
                    "Lorem Ipsum is simply dummy text of the printing and typesetting industry" +
                    "Lorem Ipsum is simply dummy text of the printing and typesetting industry"
        ) {}
    }
}