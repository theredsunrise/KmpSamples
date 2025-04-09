package org.example.kmpsamples.presentation.permissions.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kmpsamples.composeapp.generated.resources.Res
import kmpsamples.composeapp.generated.resources.button_grant_permisions
import kmpsamples.composeapp.generated.resources.button_ok
import kmpsamples.composeapp.generated.resources.dialog_title_info
import org.example.kmpsamples.presentation.TimestampState
import org.example.kmpsamples.presentation.permissions.PermissionManagerProxy
import org.example.kmpsamples.presentation.permissions.dtos.Permission
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionType
import org.example.kmpsamples.presentation.permissions.viewModel.PermissionViewModel
import org.example.kmpsamples.presentation.views.CustomDialog
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

typealias RequestedPermissionState = TimestampState<List<PermissionType>>

@Composable
fun RequestPermissions(
    requestedPermissionsState: State<RequestedPermissionState>,
    onGrantedPermissions: (permissions: List<Permission>) -> Unit,
    onDeniedPermissions: (permissions: List<Permission>) -> Unit,
) {

    val manager = koinInject<PermissionManagerProxy>()
    val viewModel = koinViewModel<PermissionViewModel>()
    val onGrantedPermissionsState by rememberUpdatedState(onGrantedPermissions)
    val onDeniedPermissionsState by rememberUpdatedState(onDeniedPermissions)

    var showRequestedPermissionState by remember { mutableStateOf<PermissionStateType?>(null) }

    LaunchedEffect(requestedPermissionsState.value) {
        val permissionTypes =
            manager.createPlatformSpecificPermissions(requestedPermissionsState.value.entity)
        viewModel.requestPermissions(permissionTypes)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    println("**** Registered")
                    manager.onPermissionState { permissionState ->
                        val isGranted = permissionState?.isGranted == true
                        showRequestedPermissionState =
                            if (isGranted) null else permissionState
                        if (isGranted) {
                            viewModel.requestNextPermission()
                        }
                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    println("**** Unregistered")
                    manager.onPermissionState(null)
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(viewModel.requestedPermissionState.value) {
        val group =
            manager.checkPermissions(viewModel.permissionsToRequest)
                .groupBy { it.isGranted }
        group[true]?.also { states ->
            val permissions = states.map { it.permission }
            println("**** Granted: $permissions")
            onGrantedPermissionsState(permissions)
        }
        group[false]?.also { states ->
            val permissions = states.map { it.permission }
            println("**** Denied: $permissions")
            onDeniedPermissionsState(permissions)
        }

        viewModel.requestedPermissionState.value?.also { uiState ->
            manager.requestPermission(uiState.entity)
        } ?: run {
            showRequestedPermissionState = null
        }
    }

    showRequestedPermissionState?.also { permissionState ->
        val confirmString =
            if (permissionState.shouldShowRationale)
                stringResource(Res.string.button_ok) else
                stringResource(Res.string.button_grant_permisions)
        CustomDialog(
            remember { mutableStateOf(true) },
            stringResource(Res.string.dialog_title_info),
            confirmString,
            permissionState.showDescription,
            onCancel = {
                viewModel.requestNextPermission()
            }
        ) {
            if (permissionState.shouldShowRationale) {
                viewModel.requestPermissionAgain()
            } else {
                manager.openGrantPermissionsScreen()
            }
        }
    }
}