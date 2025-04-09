package org.example.kmpsamples.presentation.permissions.mock

import org.example.kmpsamples.presentation.permissions.PermissionManagerInterface
import org.example.kmpsamples.presentation.permissions.dtos.PermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionType
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType

class MockPermissionManager : PermissionManagerInterface {
    override fun onPermissionState(onPermissionState: ((PermissionStateType) -> Unit)?) {
    }

    override fun checkPermissions(permissionTypes: List<PlatformPermissionType>): List<PermissionState> =
        emptyList()

    override fun requestPermission(permissionType: PlatformPermissionType) {}
    override fun createPlatformSpecificPermissions(permissionTypes: List<PermissionType>): List<PlatformPermissionType> =
        emptyList()

    override fun openGrantPermissionsScreen() {}
}
