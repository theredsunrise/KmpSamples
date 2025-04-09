package org.example.kmpsamples.presentation.permissions

import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformMultiplePermissions
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformSinglePermission

object PlatformPermissionMapper {
    fun toPlatformSinglePermissions(platformPermissionTypes: List<PlatformPermissionType>): List<PlatformSinglePermission> {
        return platformPermissionTypes.flatMap { permissionType ->
            when (permissionType) {
                is PlatformSinglePermission -> listOf(
                    permissionType
                )

                is PlatformMultiplePermissions -> permissionType.permissions
            }
        }
    }
}
