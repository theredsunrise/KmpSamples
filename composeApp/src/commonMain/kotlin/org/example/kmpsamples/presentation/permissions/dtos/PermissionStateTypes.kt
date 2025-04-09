package org.example.kmpsamples.presentation.permissions.dtos

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@Stable
@Immutable
@ObjCName(swiftName = "PermissionState")
data class PermissionState(
    val permission: Permission,
    val isGranted: Boolean,
    val shouldShowRationale: Boolean
) {
    val showDescription: String
        get() {
            return with(permission) {
                if (shouldShowRationale) rationaleMessage else deniedMessage
            }
        }
}

@OptIn(ExperimentalObjCName::class)
@Stable
@Immutable
@ObjCName(swiftName = "PermissionStateType")
sealed interface PermissionStateType {
    val isGranted: Boolean
    val shouldShowRationale: Boolean
    val showDescription: String

    @Stable
    @Immutable
    @ObjCName(swiftName = "SinglePermissionState")
    data class SinglePermissionState(val permissionState: PermissionState) :
        PermissionStateType {
        override val isGranted: Boolean get() = permissionState.isGranted
        override val shouldShowRationale: Boolean get() = permissionState.shouldShowRationale
        override val showDescription: String get() = permissionState.showDescription
    }

    @Stable
    @Immutable
    @ObjCName(swiftName = "MultiplePermissionState")
    data class MultiplePermissionState(val permissionStates: List<SinglePermissionState>) :
        PermissionStateType {
        override val isGranted: Boolean get() = permissionStates.all { it.isGranted }
        override val shouldShowRationale: Boolean get() = permissionStates.any { it.shouldShowRationale }
        override val showDescription: String get() = permissionStates.joinToString("\n\n") { it.showDescription }
    }
}