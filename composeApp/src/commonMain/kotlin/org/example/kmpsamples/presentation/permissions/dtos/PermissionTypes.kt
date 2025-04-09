package org.example.kmpsamples.presentation.permissions.dtos

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import org.example.kmpsamples.presentation.permissions.dtos.PermissionType.MultiplePermissions
import org.example.kmpsamples.presentation.permissions.dtos.PermissionType.SinglePermission
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@Stable
@Immutable
@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "PermissionType")
sealed interface PermissionType {
    @Stable
    @Immutable
    @ObjCName(swiftName = "SinglePermission")
    data class SinglePermission(val permission: Permission) :
        PermissionType

    @Stable
    @Immutable
    @ObjCName(swiftName = "MultiplePermissions")
    data class MultiplePermissions(val permissions: List<SinglePermission>) : PermissionType {
        companion object {
            fun create(vararg permissions: Permission): MultiplePermissions {
                return MultiplePermissions(
                    permissions.map { SinglePermission(it) }
                )
            }
        }
    }
}

@Stable
@Immutable
@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "PlatformPermissionType")
sealed interface PlatformPermissionType {

    @Stable
    @Immutable
    @ObjCName(swiftName = "PlatformSinglePermission")
    data class PlatformSinglePermission(val permission: Permission) :
        PlatformPermissionType {
        companion object {
            fun create(singlePermission: SinglePermission): PlatformSinglePermission {
                return PlatformSinglePermission(singlePermission.permission)
            }
        }
    }

    @Stable
    @Immutable
    @ObjCName(swiftName = "PlatformMultiplePermissions")
    data class PlatformMultiplePermissions(val permissions: List<PlatformSinglePermission>) :
        PlatformPermissionType {
        companion object {
            fun create(vararg permissions: Permission): PlatformMultiplePermissions {
                return PlatformMultiplePermissions(
                    permissions.map { PlatformSinglePermission(it) }
                )
            }

            fun create(multiplePermissions: MultiplePermissions): PlatformMultiplePermissions {
                return PlatformMultiplePermissions(
                    multiplePermissions.permissions.map { PlatformSinglePermission.create(it) }
                )
            }
        }
    }

    companion object {
        fun create(permissionType: PermissionType): PlatformPermissionType {
            return when (permissionType) {
                is SinglePermission -> PlatformSinglePermission.create(permissionType)
                is MultiplePermissions -> PlatformMultiplePermissions.create(
                    permissionType
                )
            }
        }
    }
}