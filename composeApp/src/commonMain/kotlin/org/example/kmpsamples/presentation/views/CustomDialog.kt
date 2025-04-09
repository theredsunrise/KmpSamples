package org.example.kmpsamples.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.example.kmpsamples.presentation.fillMaxWidthModifier

@Composable
fun CustomDialog(
    visibilityState: MutableState<Boolean>,
    titleString: String,
    confirmString: String,
    message: String,
    onDismissRequest: (() -> Unit)? = null,
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest?.invoke()
            visibilityState.value = false
        }, properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = onDismissRequest != null,
            usePlatformDefaultWidth = true
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = fillMaxWidthModifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = titleString,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp, 10.dp, 16.dp, 16.dp),
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp)
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors()
                            .copy(contentColor = MaterialTheme.colorScheme.inverseOnSurface),
                        onClick = {
                            onCancel()
                            visibilityState.value = false
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .padding(start = 16.dp),
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = "Cancel"
                        )
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors().copy(
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        onClick = {
                            onConfirm()
                            visibilityState.value = false
                        },
                        modifier = Modifier
                            .defaultMinSize(minWidth = 140.dp)
                            .padding(8.dp)
                            .padding(end = 16.dp),
                    ) {
                        Text(
                            confirmString,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}