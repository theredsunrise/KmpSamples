package org.example.kmpsamples.presentation.pickers

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.kmpsamples.shared.ResultState
import org.example.kmpsamples.shared.ResultState.Failure
import org.example.kmpsamples.shared.ResultState.None

@Composable
actual fun rememberGalleryPickerManager(): GalleryPickerManagerInterface {
    val mimeType = "image/*"
    val context = LocalContext.current
    val state = remember {
        MutableStateFlow<ResultState<PlatformImage>>(None)
    }
    val contentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            try {
                uri?.let { context.contentResolver.openInputStream(it) }?.let { stream ->
                    stream.use {
                        BitmapFactory.decodeStream(it)
                    }
                }?.let {
                    state.tryEmit(ResultState.Success(it.toPlatformImage()))
                } ?: run {
                    state.tryEmit(None)
                }
            } catch (e: Exception) {
                state.tryEmit(Failure(e))
            }
        }

    return remember {
        GalleryPickerManagerAndroid(state.asStateFlow()) {
            if (state.value == ResultState.Progress) return@GalleryPickerManagerAndroid
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = mimeType
            }
            if (intent.resolveActivity(context.packageManager) == null) {
                state.tryEmit(None)
                return@GalleryPickerManagerAndroid
            }
            state.tryEmit(ResultState.Progress)
            contentLauncher.launch(mimeType)
        }
    }
}