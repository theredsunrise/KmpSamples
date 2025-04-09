package org.example.kmpsamples.presentation.permissions.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.kmpsamples.presentation.TimestampState
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType

typealias PermissionTypeUIState = TimestampState<PlatformPermissionType>

class PermissionViewModel : ViewModel() {

    private var permissionsToRequestQueue = mutableListOf<PlatformPermissionType>()
    private var _requestedPermissionState = mutableStateOf<PermissionTypeUIState?>(null)

    var permissionsToRequest: List<PlatformPermissionType> = emptyList()
        private set
    val requestedPermissionState: State<PermissionTypeUIState?> = _requestedPermissionState

    fun requestPermissions(permissionTypes: List<PlatformPermissionType>) {
        permissionsToRequestQueue = permissionTypes.toMutableList()
        permissionsToRequest = permissionTypes.toList()
        requestPermissionAgain()
    }

    private fun takeFirstPermissionFromQueue() {
        viewModelScope.launch(Dispatchers.Main) {
            _requestedPermissionState.value =
                PermissionTypeUIState.createOrNull(permissionsToRequestQueue.firstOrNull())
        }
    }

    fun requestNextPermission() {
        viewModelScope.launch(Dispatchers.Main) {
            permissionsToRequestQueue.removeFirstOrNull()
            takeFirstPermissionFromQueue()
        }
    }

    fun requestPermissionAgain() {
        takeFirstPermissionFromQueue()
    }
}