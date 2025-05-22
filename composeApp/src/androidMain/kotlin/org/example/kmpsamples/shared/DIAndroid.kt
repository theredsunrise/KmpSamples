package org.example.kmpsamples.shared

import android.content.Context
import androidx.room.RoomDatabase
import org.example.kmpsamples.infrastructure.data.room.CryptocurrencyDatabase
import org.example.kmpsamples.infrastructure.data.room.getDatabaseBuilder
import org.example.kmpsamples.presentation.permissions.PermissionManagerProxy
import org.example.kmpsamples.presentation.video.ExoPlayerPool
import org.example.kmpsamples.presentation.video.VideoLooperViewFactory
import org.example.kmpsamples.presentation.video.VideoLooperViewFactoryInterface
import org.koin.dsl.bind
import org.koin.dsl.module

fun androidModules(appContext: Context) = module {
    single<PermissionManagerProxy> { PermissionManagerProxy() }
    single<RoomDatabase.Builder<CryptocurrencyDatabase>> {
        getDatabaseBuilder<CryptocurrencyDatabase>(appContext)
    }
    factory<VideoLooperViewFactoryInterface> {
        VideoLooperViewFactory(ExoPlayerPool(appContext))
    } bind VideoLooperViewFactoryInterface::class
}
