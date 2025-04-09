package org.example.kmpsamples.permissions.systemPermissionHandlers

import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformSinglePermission


interface SystemPermissionHandler {
    fun resolvePermissionState(): PermissionStateType
    fun canHandle(simplePlatformPermission: PlatformSinglePermission): Boolean
    fun requestPermission(onRequest: (permissionState: PermissionStateType) -> Unit)
}