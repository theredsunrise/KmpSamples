package org.example.kmpsamples.presentation.pickers

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

@Immutable
class PlatformImageAndroid(private val bitmap: Bitmap) : PlatformImage {
    private val imageBitmap = bitmap.asImageBitmap()

    override fun imageBitmap(): ImageBitmap {
        return imageBitmap
    }

    override fun recycle() {
        bitmap.apply {
            if (!isRecycled) {
                println("**** Recycle bitmap")
                recycle()
            }
        }
    }
}

fun Bitmap.toPlatformImage(): PlatformImage {
    return PlatformImageAndroid(this)
}