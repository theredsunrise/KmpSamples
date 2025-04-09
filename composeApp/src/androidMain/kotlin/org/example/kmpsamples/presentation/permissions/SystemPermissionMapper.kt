package org.example.kmpsamples.presentation.permissions

import android.Manifest
import org.example.kmpsamples.presentation.permissions.dtos.Permission
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformMultiplePermissions
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformSinglePermission

interface SystemPermissionMapperInterface {
    fun toSystemPermissions(permissionType: PlatformPermissionType): List<String>
    fun toSystemPermissions(permission: Permission): String
    fun toPermission(systemPermission: String): Permission
}

object SystemPermissionMapper : SystemPermissionMapperInterface {
    override fun toSystemPermissions(permissionType: PlatformPermissionType): List<String> {
        return when (permissionType) {
            is PlatformSinglePermission -> listOf(toSystemPermissions(permissionType.permission))
            is PlatformMultiplePermissions -> permissionType.permissions.map {
                toSystemPermissions(
                    it.permission
                )
            }
        }
    }

    override fun toSystemPermissions(permission: Permission): String {
        return when (permission) {
            is Permission.ContactsReadPermission -> Manifest.permission.READ_CONTACTS
            is Permission.ContactsWritePermission -> Manifest.permission.WRITE_CONTACTS
            is Permission.CalendarReadPermission -> Manifest.permission.READ_CALENDAR
            is Permission.CalendarWritePermission -> Manifest.permission.WRITE_CALENDAR
            is Permission.RecordAudioPermission -> Manifest.permission.RECORD_AUDIO
        }
    }

    override fun toPermission(systemPermission: String): Permission {
        return when (systemPermission) {
            Manifest.permission.READ_CONTACTS -> Permission.ContactsReadPermission()
            Manifest.permission.WRITE_CONTACTS -> Permission.ContactsWritePermission()
            Manifest.permission.READ_CALENDAR -> Permission.CalendarReadPermission()
            Manifest.permission.WRITE_CALENDAR -> Permission.CalendarWritePermission()
            Manifest.permission.RECORD_AUDIO -> Permission.RecordAudioPermission()
            else -> error("Not supported system permission: ${systemPermission}")
        }
    }
}