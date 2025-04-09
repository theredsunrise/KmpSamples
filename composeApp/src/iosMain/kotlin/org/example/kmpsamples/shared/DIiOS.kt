package org.example.kmpsamples.shared

import androidx.room.RoomDatabase
import org.example.kmpsamples.infrastructure.data.room.CryptocurrencyDatabase
import org.example.kmpsamples.infrastructure.data.room.getDatabaseBuilder
import org.example.kmpsamples.presentation.permissions.IOSPermissionManager
import org.example.kmpsamples.presentation.permissions.PermissionManagerInterface
import org.example.kmpsamples.presentation.permissions.PermissionManagerProxy
import org.example.kmpsamples.presentation.permissions.systemPermissionHandlers.SystemPermissionHandlerLocator
import org.koin.core.module.Module
import org.koin.dsl.module

private fun createIosModules(nativeInstances: Map<String, Any>): List<Module> {
    return listOf(
        module {
            single<RoomDatabase.Builder<CryptocurrencyDatabase>> { getDatabaseBuilder<CryptocurrencyDatabase>() }
        },
        // Uncomment either of the methods below depending on whether you want the kotlin interop or native iOS implementation.
        createPermissionManagerKotlin(),
        //createPermissionManagerNative(nativeInstances)
    )
}

//Interop version
private fun createPermissionManagerKotlin(): Module = module {
    single<PermissionManagerProxy> {
        PermissionManagerProxy().apply {
            setManager(
                IOSPermissionManager(SystemPermissionHandlerLocator())
            )
        }
    }
}

//Native iOS version
private fun createPermissionManagerNative(nativeInstances: Map<String, Any>): Module = module {
    val permissionManagerInterface =
        nativeInstances["PermissionManagerProxy"] as PermissionManagerInterface
    single<PermissionManagerProxy> {
        PermissionManagerProxy().apply {
            setManager(
                permissionManagerInterface
            )
        }
    }
}

fun initKoinIos(nativeInstancies: () -> Map<String, Any>) {
    initKoin().koin.loadModules(createIosModules(nativeInstancies()))
}