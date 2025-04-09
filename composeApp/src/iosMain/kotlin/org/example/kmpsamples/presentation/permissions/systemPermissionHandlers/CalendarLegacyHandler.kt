package org.example.kmpsamples.presentation.permissions.systemPermissionHandlers

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.example.kmpsamples.permissions.systemPermissionHandlers.SystemPermissionHandler
import org.example.kmpsamples.presentation.permissions.dtos.Permission
import org.example.kmpsamples.presentation.permissions.dtos.Permission.CalendarReadPermission
import org.example.kmpsamples.presentation.permissions.dtos.Permission.CalendarWritePermission
import org.example.kmpsamples.presentation.permissions.dtos.PermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType.MultiplePermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType.SinglePermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformSinglePermission
import platform.EventKit.EKAuthorizationStatusAuthorized
import platform.EventKit.EKAuthorizationStatusDenied
import platform.EventKit.EKAuthorizationStatusNotDetermined
import platform.EventKit.EKAuthorizationStatusRestricted
import platform.EventKit.EKEntityType
import platform.EventKit.EKEventStore
import platform.Foundation.NSProcessInfo

class CalendarLegacyHandler() : SystemPermissionHandler {
    private val mainScope = MainScope()
    private val eventStore = EKEventStore()

    override fun canHandle(simplePlatformPermission: PlatformSinglePermission): Boolean {
        fun isSupported(permission: Permission): Boolean = when (permission) {
            is CalendarReadPermission, is CalendarWritePermission -> !isAtLeastIOS(
                17
            )

            else -> false
        }
        return isSupported(simplePlatformPermission.permission)
    }

    private fun createMultiplePermissionState(
        isGranted: Boolean,
        shouldShowRationale: Boolean
    ): MultiplePermissionState {
        val permissionStates = listOf(CalendarReadPermission(), CalendarWritePermission()).map {
            val permissionState = PermissionState(it, isGranted, shouldShowRationale)
            SinglePermissionState(permissionState)
        }
        return MultiplePermissionState(permissionStates)
    }

    override fun resolvePermissionState(): PermissionStateType {
        val status = EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent)
        return when (status) {
            EKAuthorizationStatusAuthorized -> createMultiplePermissionState(true, false)
            EKAuthorizationStatusDenied -> createMultiplePermissionState(false, false)
            EKAuthorizationStatusNotDetermined -> createMultiplePermissionState(false, true)
            EKAuthorizationStatusRestricted -> createMultiplePermissionState(false, false)
            else -> createMultiplePermissionState(false, true)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun isAtLeastIOS(version: Int): Boolean {
        val majorVersion =
            NSProcessInfo.processInfo.operatingSystemVersion.useContents { majorVersion }
        return majorVersion >= version
    }

    override fun requestPermission(onRequest: (permissionState: PermissionStateType) -> Unit) {
        eventStore.requestAccessToEntityType(EKEntityType.EKEntityTypeEvent) { isGranted, error ->
            mainScope.launch {
                onRequest(resolvePermissionState())
            }
        }
    }
}