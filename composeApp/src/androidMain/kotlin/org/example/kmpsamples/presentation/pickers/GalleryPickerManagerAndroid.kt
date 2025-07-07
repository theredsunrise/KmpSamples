package org.example.kmpsamples.presentation.pickers

import kotlinx.coroutines.flow.StateFlow
import org.example.kmpsamples.shared.ResultState

class GalleryPickerManagerAndroid(
    override val state: StateFlow<ResultState<PlatformImage>>,
    private val onPresentPicker: () -> Unit,
) :
    GalleryPickerManagerInterface {
    override fun presentPicker() {
        onPresentPicker()
    }
}