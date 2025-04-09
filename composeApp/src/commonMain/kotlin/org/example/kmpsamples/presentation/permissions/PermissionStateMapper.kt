package org.example.kmpsamples.presentation.permissions

import org.example.kmpsamples.presentation.permissions.dtos.PermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType.MultiplePermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType.SinglePermissionState

object PermissionStateMapper {
    fun toPermissionStates(permissionStateTypes: List<PermissionStateType>): List<PermissionState> {
        return permissionStateTypes.flatMap { type ->
            when (type) {
                is SinglePermissionState -> listOf(type.permissionState)
                is MultiplePermissionState -> type.permissionStates.map { it.permissionState }
            }
        }
    }
}