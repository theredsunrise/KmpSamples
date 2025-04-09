package org.example.kmpsamples.presentation.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.button_grant_permisions
import org.example.kmpsamples.presentation.permissions.dtos.Permission
import org.example.kmpsamples.presentation.permissions.dtos.Permission.CalendarReadPermission
import org.example.kmpsamples.presentation.permissions.dtos.Permission.CalendarWritePermission
import org.example.kmpsamples.presentation.permissions.dtos.Permission.ContactsReadPermission
import org.example.kmpsamples.presentation.permissions.dtos.Permission.ContactsWritePermission
import org.example.kmpsamples.presentation.permissions.dtos.Permission.RecordAudioPermission
import org.example.kmpsamples.presentation.permissions.dtos.PermissionType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionType.SinglePermission
import org.example.kmpsamples.presentation.permissions.views.RequestPermissions
import org.example.kmpsamples.presentation.permissions.views.RequestedPermissionState
import org.jetbrains.compose.resources.stringResource

private val iconModifier = Modifier.requiredSize(90.dp, 90.dp)

private fun List<Permission>.containsOccurrences(vararg permissions: Permission): Int {
    return this.count { permissions.contains(it) }
}

@Composable
fun PermissionsScreen(modifier: Modifier) {
    val grantedPermissionsState = remember { mutableStateOf((emptyList<Permission>())) }
    val deniedPermissionsState = remember { mutableStateOf((emptyList<Permission>())) }
    val requestPermissionsTrigger = remember {
        mutableStateOf<RequestedPermissionState>(RequestedPermissionState.create(emptyList()))
    }

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val calendarColor: Color = when (grantedPermissionsState.value.containsOccurrences(
            CalendarReadPermission(), CalendarWritePermission()
        )) {
            0 -> Color.DarkGray
            1 -> Color.Gray
            else -> Color.Green
        }

        val callColor: Color = when (grantedPermissionsState.value.containsOccurrences(
            ContactsReadPermission(), ContactsWritePermission()
        )) {
            0 -> Color.DarkGray
            1 -> Color.Gray
            else -> Color.Green
        }

        val micColor: Color =
            when (grantedPermissionsState.value.containsOccurrences(RecordAudioPermission())) {
                0 -> Color.DarkGray
                else -> Color.Green
            }

        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                "Calendar",
                modifier = iconModifier,
                tint = calendarColor
            )
            Icon(
                imageVector = Icons.Outlined.Call,
                "Call",
                modifier = iconModifier,
                tint = callColor
            )
            Icon(
                imageVector = Icons.Outlined.Mic,
                "Mic",
                modifier = iconModifier,
                tint = micColor
            )
        }

        Spacer(Modifier.requiredHeight(20.dp))

        Button(onClick = {
            requestPermissionsTrigger.value = RequestedPermissionState.create(
                listOf(
                    PermissionType.MultiplePermissions.create(
                        ContactsReadPermission(),
                        ContactsWritePermission()
                    ),
                    PermissionType.MultiplePermissions.create(
                        CalendarWritePermission(),
                        CalendarReadPermission()
                    ),
                    SinglePermission(RecordAudioPermission())
                )
            )
        }) {
            Text(stringResource(Res.string.button_grant_permisions))
        }
    }

    RequestPermissions(
        requestPermissionsTrigger,
        { grantedPermissionsState.value = it },
        { deniedPermissionsState.value = it })
}

