package org.example.kmpsamples.presentation.deepLinks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import org.example.kmpsamples.presentation.deepLinks.DeepLinkViewModel.Event
import org.example.kmpsamples.presentation.deepLinks.DeepLinkViewModel.Intent
import org.example.kmpsamples.presentation.deepLinks.DeepLinkViewModel.Intent.Process
import org.example.kmpsamples.presentation.getWindowSizeClass
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun DeepLinkScreen(
    modifier: Modifier,
    id: String,
    uiEventFlow: Flow<Event>,
    onIntent: (Intent) -> Unit,
    onUiEvent: (Event) -> Unit,
) {

    val owner = LocalLifecycleOwner.current
    val windowSizeClass = getWindowSizeClass()
    val onUiEventState by rememberUpdatedState(onUiEvent)

    LaunchedEffect(owner.lifecycle, windowSizeClass) {
        launch(Dispatchers.Main.immediate) {
            try {
                owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiEventFlow
                        .onCompletion { cause ->
                            if (cause is CancellationException) {
                                println("**** Flow was cancelled (subscriber disconnected).")
                            } else if (cause == null) {
                                println("**** Flow completed normally.")
                            }
                        }
                        .collectLatest { event ->
                            println("**** Event received.")
                            onUiEventState(event)
                        }
                }
            } catch (_: Exception) {
                println("**** Coroutine cancelled")
                currentCoroutineContext().ensureActive()
            }
        }
    }

    Box(modifier, contentAlignment = Alignment.Center) {
        Column(Modifier.width(IntrinsicSize.Max)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = id,
                textAlign = TextAlign.Center
            )
            Button(onClick = { onIntent(Process) }) {
                Text("Test")
            }
        }
    }
}