package org.example.kmpsamples.presentation.pickers

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap

@Immutable
class PlatformImageIos(private val imageBitmap: ImageBitmap) : PlatformImage {
    override fun imageBitmap(): ImageBitmap = imageBitmap
    override fun recycle() {}
}