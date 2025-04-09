package org.example.kmpsamples.shared

import android.content.Context
import androidx.room.RoomDatabase
import org.example.kmpsamples.infrastructure.data.room.CryptocurrencyDatabase
import org.example.kmpsamples.infrastructure.data.room.getDatabaseBuilder
import org.example.kmpsamples.presentation.permissions.PermissionManagerProxy
import org.koin.dsl.module

fun androidModules(appContext: Context) = module {
    single<PermissionManagerProxy> { PermissionManagerProxy() }
    single<RoomDatabase.Builder<CryptocurrencyDatabase>> {
        getDatabaseBuilder<CryptocurrencyDatabase>(appContext)
    }
}
