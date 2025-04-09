package org.example.kmpsamples.presentation.permissions.systemPermissionHandlers

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.example.kmpsamples.permissions.systemPermissionHandlers.SystemPermissionHandler
import org.example.kmpsamples.presentation.permissions.dtos.Permission
import org.example.kmpsamples.presentation.permissions.dtos.PermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType.SinglePermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformSinglePermission
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionDenied
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted
import platform.AVFAudio.AVAudioSessionRecordPermissionUndetermined

class RecordAudioHandler() : SystemPermissionHandler {
    private val mainScope = MainScope()

    override fun canHandle(simplePlatformPermission: PlatformSinglePermission): Boolean {
        fun isSupported(permission: Permission): Boolean = when (permission) {
            is Permission.RecordAudioPermission -> true
            else -> false
        }
        return isSupported(simplePlatformPermission.permission)
    }

    private fun createSimplePermissionState(
        isGranted: Boolean,
        shouldShowRationale: Boolean
    ): SinglePermissionState {
        val permissionState =
            PermissionState(Permission.RecordAudioPermission(), isGranted, shouldShowRationale)
        return SinglePermissionState(permissionState)
    }

    override fun resolvePermissionState(): PermissionStateType {
        val status = AVAudioSession.sharedInstance().recordPermission
        return when (status) {
            AVAudioSessionRecordPermissionGranted -> createSimplePermissionState(true, false)
            AVAudioSessionRecordPermissionDenied -> createSimplePermissionState(false, false)
            AVAudioSessionRecordPermissionUndetermined -> createSimplePermissionState(false, true)
            else -> createSimplePermissionState(false, true)
        }
    }

    override fun requestPermission(onRequest: (permissionState: PermissionStateType) -> Unit) {
        AVAudioSession.sharedInstance().requestRecordPermission { isGranted ->
            mainScope.launch {
                onRequest(resolvePermissionState())
            }
        }
    }
}