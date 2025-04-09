@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.example.kmpsamples.presentation.permissions

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.example.kmpsamples.presentation.permissions.dtos.Permission
import org.example.kmpsamples.presentation.permissions.dtos.PermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType
import org.example.kmpsamples.presentation.permissions.dtos.PermissionStateType.SinglePermissionState
import org.example.kmpsamples.presentation.permissions.dtos.PermissionType
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformMultiplePermissions
import org.example.kmpsamples.presentation.permissions.dtos.PlatformPermissionType.PlatformSinglePermission

actual interface PermissionManagerInterface {
    actual fun onPermissionState(onPermissionState: ((PermissionStateType) -> Unit)?)
    actual fun checkPermissions(permissionTypes: List<PlatformPermissionType>): List<PermissionState>
    actual fun requestPermission(permissionType: PlatformPermissionType)
    actual fun createPlatformSpecificPermissions(permissionTypes: List<PermissionType>): List<PlatformPermissionType>
    actual fun openGrantPermissionsScreen()
}

class AndroidPermissionManager(private val systemPermissionMapper: SystemPermissionMapperInterface) :
    PermissionManagerInterface, DefaultLifecycleObserver {

    private var activityInVisibleLifecycle: ComponentActivity? = null
    private var multiplePermissionsLauncher: ActivityResultLauncher<Array<String>>? = null
    private var onPermissionState: ((PermissionStateType) -> Unit)? = null

    override fun onPermissionState(onPermissionState: ((PermissionStateType) -> Unit)?) {
        this.onPermissionState = onPermissionState
    }

    override fun createPlatformSpecificPermissions(permissionTypes: List<PermissionType>): List<PlatformPermissionType> {
        fun throwErrorIfNotSupported(permissionType: PlatformPermissionType) {
            systemPermissionMapper.toSystemPermissions(permissionType)
        }
        return permissionTypes.map {
            val result = PlatformPermissionType.create(it)
            throwErrorIfNotSupported(result)
            result
        }
    }

    override fun checkPermissions(permissionTypes: List<PlatformPermissionType>): List<PermissionState> {
        val activity = this.activityInVisibleLifecycle ?: return emptyList()
        return PermissionStateMapper.toPermissionStates(
            resolvePermissionStates(activity, permissionTypes)
        )
    }

    override fun requestPermission(permissionType: PlatformPermissionType) {
        val multiplePermissionsLauncher = this.multiplePermissionsLauncher ?: return
        val activity = this.activityInVisibleLifecycle ?: return
        val onPermissionState = this.onPermissionState ?: return
        val requestedPermissionState =
            resolvePermissionStates(activity, listOf(permissionType)).first()
        if (requestedPermissionState.isGranted) {
            onPermissionState(requestedPermissionState)
        } else {
            val systemPermissions =
                systemPermissionMapper.toSystemPermissions(permissionType).toTypedArray()
            multiplePermissionsLauncher.launch(systemPermissions)
        }
    }

    override fun openGrantPermissionsScreen() {
        val activity = this.activityInVisibleLifecycle ?: return
        fun launchIntent(action: String): Boolean {
            val intent = Intent(
                action, Uri.fromParts("package", activity.packageName, null)
            )
            intent.takeIf { it.resolveActivity(activity.packageManager) != null }?.also {
                activity.startActivity(it)
                return true
            }
            return false
        }
        arrayOf(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Settings.ACTION_APPLICATION_SETTINGS
        ).firstOrNull { (launchIntent(it)) }
    }

    private fun toPlatformPermissionType(permissions: List<Permission>): PlatformPermissionType {
        check(permissions.isNotEmpty())
        return when {
            permissions.count() == 1 -> PlatformSinglePermission(permissions.first())
            else -> PlatformMultiplePermissions(permissions.map {
                PlatformSinglePermission(
                    it
                )
            })
        }
    }

    private fun resolvePermissionStates(
        activity: ComponentActivity, permissionTypes: List<PlatformPermissionType>
    ): List<PermissionStateType> {
        return permissionTypes.map { permissionType ->
            when (permissionType) {
                is PlatformSinglePermission -> resolveSinglePermissionState(
                    activity, permissionType
                )

                is PlatformMultiplePermissions -> {
                    val permissionStates = permissionType.permissions.map {
                        resolveSinglePermissionState(activity, it)
                    }
                    PermissionStateType.MultiplePermissionState(permissionStates)
                }
            }
        }
    }

    private fun resolveSinglePermissionState(
        activity: ComponentActivity, singlePermission: PlatformSinglePermission

    ): SinglePermissionState {
        val permission = singlePermission.permission
        val systemPermission = systemPermissionMapper.toSystemPermissions(permission)
        val permissionState = when {
            ContextCompat.checkSelfPermission(
                activity, systemPermission
            ) == PackageManager.PERMISSION_GRANTED -> PermissionState(
                permission, true, false
            )

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity, systemPermission
            ) -> PermissionState(permission, false, true)

            else -> PermissionState(permission, false, false)
        }
        return SinglePermissionState(permissionState)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        val activity = (owner as? ComponentActivity) ?: return
        val multiplePermissionsLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { mapOfGrants ->
                val activity = this.activityInVisibleLifecycle ?: return@registerForActivityResult
                val onPermissionState = this.onPermissionState ?: return@registerForActivityResult

                val requestedPermissions = mapOfGrants.keys.sorted().map {
                    systemPermissionMapper.toPermission(it)
                }
                val requestedPermissionType = toPlatformPermissionType(requestedPermissions)
                val requestedPermissionState = resolvePermissionStates(
                    activity, listOf(requestedPermissionType)
                ).first()
                onPermissionState(requestedPermissionState)
            }
        this.multiplePermissionsLauncher = multiplePermissionsLauncher
    }

    override fun onDestroy(owner: LifecycleOwner) {
        this.multiplePermissionsLauncher?.unregister()
        super.onDestroy(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        val activity = (owner as? ComponentActivity) ?: return
        this.activityInVisibleLifecycle = activity
    }

    override fun onStop(owner: LifecycleOwner) {
        activityInVisibleLifecycle = null
        super.onStop(owner)
    }
}