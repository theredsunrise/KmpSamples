package org.example.kmpsamples.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.menu_data_collecting
import kmpsamples.composeapp.generated.resources.menu_permissions
import kmpsamples.composeapp.generated.resources.menu_transitions
import kmpsamples.composeapp.generated.resources.menu_video
import org.jetbrains.compose.resources.stringResource

@Composable
fun MenuScreen(
    modifier: Modifier,
    onPermissions: () -> Unit,
    onCryptoCurrencies: () -> Unit,
    onVideo: () -> Unit,
    onTransitions: () -> Unit
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onPermissions) {
            Text(stringResource(Res.string.menu_permissions))
        }
        Spacer(Modifier.requiredHeight(20.dp))
        Button(onClick = onCryptoCurrencies) {
            Text(stringResource(Res.string.menu_data_collecting))
        }
        Spacer(Modifier.requiredHeight(20.dp))
        Button(onClick = onVideo) {
            Text(stringResource(Res.string.menu_video))
        }
        Spacer(Modifier.requiredHeight(20.dp))
        Button(onClick = onTransitions) {
            Text(stringResource(Res.string.menu_transitions))
        }
    }

}
