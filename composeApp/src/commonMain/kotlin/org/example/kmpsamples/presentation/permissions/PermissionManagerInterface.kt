package org.example.kmpsamples.presentation.permissions

import org.example.kmpsamples.presentation.permissions.dtos.PermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionType
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface PermissionManagerInterface {
    fun onPermissionState(onPermissionState: ((PermissionStateType) -> Unit)?)
    fun checkPermissions(permissionTypes: List<PlatformPermissionType>): List<PermissionState>
    fun requestPermission(permissionType: PlatformPermissionType)
    fun createPlatformSpecificPermissions(permissionTypes: List<PermissionType>): List<PlatformPermissionType>
    fun openGrantPermissionsScreen()
}

class PermissionManagerProxy : PermissionManagerInterface {
    private lateinit var manager: PermissionManagerInterface

    fun setManager(manager: PermissionManagerInterface) {
        this.manager = manager
    }

    override fun requestPermission(permissionType: PlatformPermissionType) {
        manager.requestPermission(permissionType)
    }

    override fun checkPermissions(permissionTypes: List<PlatformPermissionType>): List<PermissionState> {
        return manager.checkPermissions(permissionTypes)
    }

    override fun onPermissionState(onPermissionState: ((PermissionStateType) -> Unit)?) {
        manager.onPermissionState(onPermissionState)
    }

    override fun createPlatformSpecificPermissions(permissionTypes: List<PermissionType>): List<PlatformPermissionType> {
        return manager.createPlatformSpecificPermissions(permissionTypes)
    }

    override fun openGrantPermissionsScreen() {
        manager.openGrantPermissionsScreen()
    }
}
