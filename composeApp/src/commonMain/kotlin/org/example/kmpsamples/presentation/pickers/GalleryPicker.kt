package org.example.kmpsamples.presentation.pickers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.StateFlow
import org.example.kmpsamples.shared.ResultState

@Immutable
interface PlatformImage {
    fun imageBitmap(): ImageBitmap
    fun recycle()
}

@Immutable
interface GalleryPickerManagerInterface {
    val state: StateFlow<ResultState<PlatformImage>>
    fun presentPicker()
}

@Composable
expect fun rememberGalleryPickerManager(): GalleryPickerManagerInterface