package org.example.kmpsamples.presentation.permissions.systemPermissionHandlers

import org.example.kmpsamples.permissions.systemPermissionHandlers.SystemPermissionHandler
import org.example.kmpsamples.presentation.permissions.PlatformPermissionMapper
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType

interface SystemPermissionHandlerLocatorInterface {
    fun getPermissionHandler(
        permissionType: PlatformPermissionType
    ): SystemPermissionHandler
}

class SystemPermissionHandlerLocator : SystemPermissionHandlerLocatorInterface {
    private val systemPermissionHandlers =
        setOf(
            ContactsHandler(),
            RecordAudioHandler(),
            CalendarLegacyHandler(),
            CalendarWriteAccessIos17Handler(),
            CalendarFullAccessIos17Handler()
        )

    override fun getPermissionHandler(
        platformPermissionType: PlatformPermissionType
    ): SystemPermissionHandler {
        val singlePermissions =
            PlatformPermissionMapper.toPlatformSinglePermissions(listOf(platformPermissionType))
        return systemPermissionHandlers.find { handler ->
            singlePermissions.any { handler.canHandle(it) }
        }
            ?: error("No system permission handler can handle permission type: $platformPermissionType")
    }
}