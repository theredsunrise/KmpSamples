package org.example.kmpsamples.presentation.permissions.systemPermissionHandlers

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.example.kmpsamples.permissions.systemPermissionHandlers.SystemPermissionHandler
import org.example.kmpsamples.presentation.permissions.dtos.Permission
import org.example.kmpsamples.presentation.permissions.dtos.Permission.CalendarWritePermission
import org.example.kmpsamples.presentation.permissions.dtos.PermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType.SinglePermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformSinglePermission
import platform.EventKit.EKAuthorizationStatus
import platform.EventKit.EKAuthorizationStatusAuthorized
import platform.EventKit.EKAuthorizationStatusDenied
import platform.EventKit.EKAuthorizationStatusFullAccess
import platform.EventKit.EKAuthorizationStatusNotDetermined
import platform.EventKit.EKAuthorizationStatusRestricted
import platform.EventKit.EKAuthorizationStatusWriteOnly
import platform.EventKit.EKEntityType
import platform.EventKit.EKEventStore
import platform.Foundation.NSProcessInfo

class CalendarWriteAccessIos17Handler() : SystemPermissionHandler {
    private val mainScope = MainScope()
    private val eventStore = EKEventStore()
    private var wasPermissionGranted: Boolean? = null

    override fun canHandle(simplePlatformPermission: PlatformSinglePermission): Boolean {

        fun isSupported(permission: Permission): Boolean = when (permission) {
            is CalendarWritePermission -> isAtLeastIOS(17)
            else -> false
        }
        return isSupported(simplePlatformPermission.permission)
    }

    private fun createSimplePermissionState(
        isGranted: Boolean,
        shouldShowRationale: Boolean
    ): SinglePermissionState {
        val permissionState =
            PermissionState(CalendarWritePermission(), isGranted, shouldShowRationale)
        return SinglePermissionState(permissionState)
    }

    override fun resolvePermissionState(): PermissionStateType {
        val status = getStatus()
        return when (status) {
            EKAuthorizationStatusAuthorized,
            EKAuthorizationStatusWriteOnly -> {
                createSimplePermissionState(true, false)
            }

            EKAuthorizationStatusDenied,
            EKAuthorizationStatusRestricted,
            EKAuthorizationStatusFullAccess -> {
                createSimplePermissionState(false, false)
            }

            EKAuthorizationStatusNotDetermined -> {
                createSimplePermissionState(
                    wasPermissionGranted == true,
                    wasPermissionGranted == null
                )
            }

            else -> {
                createSimplePermissionState(false, true)
            }
        }
    }

    private fun getStatus(): EKAuthorizationStatus {
        return EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun isAtLeastIOS(version: Int): Boolean {
        val majorVersion =
            NSProcessInfo.processInfo.operatingSystemVersion.useContents { majorVersion }
        return majorVersion >= version
    }

    override fun requestPermission(onRequest: (permissionState: PermissionStateType) -> Unit) {
        eventStore.requestWriteOnlyAccessToEventsWithCompletion() { isGranted, error ->
            mainScope.launch {
                if (getStatus() == EKAuthorizationStatusNotDetermined && error == null) {
                    wasPermissionGranted = isGranted
                }
                onRequest(resolvePermissionState())
            }
        }
    }
}