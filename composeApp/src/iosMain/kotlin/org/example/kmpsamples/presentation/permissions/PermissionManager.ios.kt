package org.example.kmpsamples.presentation.permissions

import org.example.kmpsamples.presentation.permissions.dtos.PermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionType
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformMultiplePermissions
import org.example.kmpsamples.presentation.permissions.systemPermissionHandlers.SystemPermissionHandlerLocatorInterface
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

actual interface PermissionManagerInterface {
    actual fun onPermissionState(onPermissionState: ((PermissionStateType) -> Unit)?)
    actual fun checkPermissions(permissionTypes: List<PlatformPermissionType>): List<PermissionState>
    actual fun requestPermission(permissionType: PlatformPermissionType)
    actual fun createPlatformSpecificPermissions(permissionTypes: List<PermissionType>): List<PlatformPermissionType>
    actual fun openGrantPermissionsScreen()
}

class IOSPermissionManager(private val systemPermissionHandlerLocator: SystemPermissionHandlerLocatorInterface) :
    PermissionManagerInterface {

    private var onPermissionState: ((PermissionStateType) -> Unit)? = null

    override fun onPermissionState(onPermissionState: ((PermissionStateType) -> Unit)?) {
        this.onPermissionState = onPermissionState
    }

    override fun createPlatformSpecificPermissions(permissionTypes: List<PermissionType>): List<PlatformPermissionType> {
        val platformPermissionTypes = permissionTypes.map { PlatformPermissionType.create(it) }
        return PlatformPermissionMapper.toPlatformSinglePermissions(platformPermissionTypes)
            .groupBy { systemPermissionHandlerLocator.getPermissionHandler(it) }
            .entries.map { entry ->
                PlatformMultiplePermissions(entry.value.toList())
            }
    }

    override fun checkPermissions(permissionTypes: List<PlatformPermissionType>): List<PermissionState> {
        return PermissionStateMapper.toPermissionStates(
            resolvePermissionStates(permissionTypes)
        )
    }

    override fun requestPermission(permissionType: PlatformPermissionType) {

        val onPermissionState = this.onPermissionState ?: return
        val requestedPermissionState = resolvePermissionStates(listOf(permissionType)).first()
        if (requestedPermissionState.isGranted) {
            onPermissionState(requestedPermissionState)
        } else {
            systemPermissionHandlerLocator.getPermissionHandler(permissionType)
                .requestPermission { resolvedPermissionState ->
                    onPermissionState(resolvedPermissionState)
                }
        }
    }

    override fun openGrantPermissionsScreen() {
        val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
        UIApplication.sharedApplication.openURL(url, options = emptyMap<Any?, Any>()) { success ->
            println("Opened app settings: $success")
        }
    }

    private fun resolvePermissionStates(
        permissionTypes: List<PlatformPermissionType>
    ): List<PermissionStateType> {
        return permissionTypes.map {
            systemPermissionHandlerLocator.getPermissionHandler(it).resolvePermissionState()
        }
    }
}




