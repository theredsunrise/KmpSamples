package org.example.kmpsamples.presentation.pickers

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.pineapple
import kotlinx.coroutines.launch
import org.example.kmpsamples.presentation.pickers.viewModel.GalleryPickerViewModel.Intents
import org.example.kmpsamples.presentation.pickers.viewModel.GalleryPickerViewModel.Intents.UpdateAvatar
import org.example.kmpsamples.presentation.pickers.viewModel.GalleryPickerViewModel.UIState
import org.example.kmpsamples.shared.ResultState
import org.example.kmpsamples.shared.ResultState.Failure
import org.example.kmpsamples.shared.ResultState.None
import org.example.kmpsamples.shared.ResultState.Progress
import org.example.kmpsamples.shared.ResultState.Success
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PickerScreen(
    modifier: Modifier,
    state: State<UIState>,
    galleryPickerManager: GalleryPickerManagerInterface,
    snackbarHostState: SnackbarHostState,
    onSendIntent: (intent: Intents) -> Unit
) {
    val onSendIntentState by rememberUpdatedState(onSendIntent)
    val pickerState by galleryPickerManager.state.collectAsStateWithLifecycle()
    var mergedState by remember { mutableStateOf<ResultState<PlatformImage?>>(None) }

    LaunchedEffect(pickerState) {
        when (val result = pickerState) {
            is Success -> onSendIntentState(UpdateAvatar(result.value))
            is Failure -> {
                launch { snackbarHostState.showSnackbar(result.exception.toString()) }
                mergedState = Success(state.value.avatar)
            }

            is Progress -> mergedState = Progress
            is None -> mergedState = Success(state.value.avatar)
        }
    }

    LaunchedEffect(state.value) {
        mergedState = Success(state.value.avatar)
    }

    Box(modifier) {
        Column(modifier.wrapContentSize().align(Alignment.Center)) {
            val shapeModifier = Modifier.border(
                2.dp, MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge
            ).clip(MaterialTheme.shapes.extraLarge)

            val imageModifier = remember {
                Modifier.fillMaxWidth(0.7f).wrapContentHeight().weight(1f, false)
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally)
            }

            if (mergedState.isProgress()) {
                CircularProgressIndicator(
                    Modifier.size(50.dp).align(Alignment.CenterHorizontally)
                )
            } else {
                mergedState.success()?.also { avatar ->
                    Image(
                        bitmap = avatar.imageBitmap(),
                        contentDescription = "",
                        modifier = imageModifier.then(shapeModifier)
                    )
                } ?: run {
                    Image(
                        imageVector = vectorResource(Res.drawable.pineapple),
                        contentDescription = "",
                        modifier = imageModifier.then(shapeModifier)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    galleryPickerManager.presentPicker()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .defaultMinSize(minHeight = 30.dp)
            ) {
                Text("Set image")
            }
        }
    }
}