package org.example.kmpsamples.presentation.pickers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.example.kmpsamples.shared.ResultState

@Composable
actual fun rememberGalleryPickerManager(): GalleryPickerManagerInterface {
    val coroutineScope = rememberCoroutineScope()
    return remember { GalleryPickerManagerWrapper(GalleryPickerManagerIos(coroutineScope)) }
}

private class GalleryPickerManagerWrapper(private val pickerManager: GalleryPickerManagerIos) :
    GalleryPickerManagerInterface {
    override val state: StateFlow<ResultState<PlatformImage>>
        get() = pickerManager.state

    override fun presentPicker() {
        pickerManager.presentPicker()
    }
}