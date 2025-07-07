package org.example.kmpsamples.presentation.composePreviews

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.hotmovies.presentation.theme.CustomMaterialTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.kmpsamples.presentation.fillMaxSizeModifier
import org.example.kmpsamples.presentation.pickers.GalleryPickerManagerInterface
import org.example.kmpsamples.presentation.pickers.PickerScreen
import org.example.kmpsamples.presentation.pickers.PlatformImage
import org.example.kmpsamples.presentation.pickers.viewModel.GalleryPickerViewModel
import org.example.kmpsamples.shared.ResultState

@Preview(device = "spec:parent=pixel_5")
@Composable
private fun PickerScreenPreview() {
    CustomMaterialTheme {
        Surface(fillMaxSizeModifier.safeDrawingPadding()) {
            val snackbarHostState = remember { SnackbarHostState() }
            val state = remember { mutableStateOf(GalleryPickerViewModel.UIState(null)) }
            PickerScreen(
                fillMaxSizeModifier, state,
                rememberMockedGalleryPickerManager(),
                snackbarHostState
            ) {
                (it as? GalleryPickerViewModel.Intents.UpdateAvatar)?.also {
                    state.value = state.value.copy(avatar = it.image)
                }
            }
        }
    }
}

private fun mockedPlatformImage(context: Context): PlatformImage {
    return object : PlatformImage {
        val imageBitmap =
            (ContextCompat.getDrawable(
                context,
                android.R.drawable.star_on
            ) as BitmapDrawable).bitmap.asImageBitmap()

        override fun imageBitmap(): ImageBitmap {
            return imageBitmap
        }

        override fun recycle() {
        }
    }
}

@Composable
private fun rememberMockedGalleryPickerManager(): GalleryPickerManagerInterface {
    val context = LocalContext.current
    return remember {
        val platformImage = mockedPlatformImage(context)
        object : GalleryPickerManagerInterface {
            override val state: StateFlow<ResultState<PlatformImage>> =
                MutableStateFlow(ResultState.Success(platformImage))

            override fun presentPicker() {
            }

        }
    }
}
