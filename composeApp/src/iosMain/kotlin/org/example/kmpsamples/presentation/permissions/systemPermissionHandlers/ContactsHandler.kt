package org.example.kmpsamples.presentation.permissions.systemPermissionHandlers

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.example.kmpsamples.permissions.systemPermissionHandlers.SystemPermissionHandler
import org.example.kmpsamples.presentation.permissions.dtos.Permission
import org.example.kmpsamples.presentation.permissions.dtos.Permission.ContactsReadPermission
import org.example.kmpsamples.presentation.permissions.dtos.Permission.ContactsWritePermission
import org.example.kmpsamples.presentation.permissions.dtos.PermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType.MultiplePermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType.SinglePermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformSinglePermission
import platform.Contacts.CNAuthorizationStatusAuthorized
import platform.Contacts.CNAuthorizationStatusDenied
import platform.Contacts.CNAuthorizationStatusLimited
import platform.Contacts.CNAuthorizationStatusNotDetermined
import platform.Contacts.CNAuthorizationStatusRestricted
import platform.Contacts.CNContactStore
import platform.Contacts.CNEntityType

class ContactsHandler() : SystemPermissionHandler {
    private val mainScope = MainScope()
    private val contactStore = CNContactStore()

    override fun canHandle(simplePlatformPermission: PlatformSinglePermission): Boolean {
        fun isSupported(permission: Permission): Boolean = when (permission) {
            is ContactsReadPermission, is ContactsWritePermission -> true
            else -> false
        }
        return isSupported(simplePlatformPermission.permission)
    }

    private fun createMultiplePermissionState(
        isGranted: Boolean,
        shouldShowRationale: Boolean
    ): MultiplePermissionState {
        val permissionStates = listOf(ContactsReadPermission(), ContactsWritePermission()).map {
            val permissionState = PermissionState(it, isGranted, shouldShowRationale)
            SinglePermissionState(permissionState)
        }
        return MultiplePermissionState(permissionStates)
    }

    override fun resolvePermissionState(): PermissionStateType {
        val status =
            CNContactStore.authorizationStatusForEntityType(CNEntityType.CNEntityTypeContacts)
        return when (status) {
            CNAuthorizationStatusLimited -> createMultiplePermissionState(true, false)
            CNAuthorizationStatusAuthorized -> createMultiplePermissionState(true, false)
            CNAuthorizationStatusDenied -> createMultiplePermissionState(false, false)
            CNAuthorizationStatusRestricted -> createMultiplePermissionState(false, false)
            CNAuthorizationStatusNotDetermined -> createMultiplePermissionState(false, true)
            else -> createMultiplePermissionState(false, true)
        }
    }

    override fun requestPermission(onRequest: (permissionState: PermissionStateType) -> Unit) {
        contactStore.requestAccessForEntityType(CNEntityType.CNEntityTypeContacts) { isGranted, error ->
            mainScope.launch {
                onRequest(resolvePermissionState())
            }
        }
    }
}