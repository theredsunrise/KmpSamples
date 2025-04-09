package org.example.kmpsamples.shared

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.example.kmpsamples.application.cryptocurrencies.CollectAndPersistCryptocurrencyDataUseCase
import org.example.kmpsamples.application.cryptocurrencies.CollectMultipleCryptocurrencyDataUseCase
import org.example.kmpsamples.application.interfaces.CryptocurrencyRepositoryInterface
import org.example.kmpsamples.application.interfaces.CryptocurrencyStorageRepositoryInterface
import org.example.kmpsamples.infrastructure.data.room.CryptocurrencyDatabase
import org.example.kmpsamples.infrastructure.data.room.RoomCryptocurrencyMapper
import org.example.kmpsamples.infrastructure.data.room.RoomCryptocurrencyStorageRepository
import org.example.kmpsamples.infrastructure.network.CryptocurrencyRemoteMapper
import org.example.kmpsamples.infrastructure.network.CryptocurrencyRepository
import org.example.kmpsamples.presentation.cryptocurrencies.viewModel.CryptocurrencyViewModel
import org.example.kmpsamples.presentation.permissions.viewModel.PermissionViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModules = module {
    single<CryptocurrencyDatabase> {
        get<RoomDatabase.Builder<CryptocurrencyDatabase>>()
            .fallbackToDestructiveMigration(true)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO).build()
    }
    single<CryptocurrencyStorageRepositoryInterface> {
        val db = get<CryptocurrencyDatabase>()
        RoomCryptocurrencyStorageRepository(
            db.klineEntityDao(),
            db.klineStatisticViewDao(),
            RoomCryptocurrencyMapper
        )
    } bind CryptocurrencyStorageRepositoryInterface::class

    single<CryptocurrencyRepositoryInterface> {
        CryptocurrencyRepository(CryptocurrencyRemoteMapper)
    } bind CryptocurrencyRepositoryInterface::class

    single<CollectAndPersistCryptocurrencyDataUseCase> {
        CollectAndPersistCryptocurrencyDataUseCase(9, get(), get())
    }

    single<CollectMultipleCryptocurrencyDataUseCase> {
        CollectMultipleCryptocurrencyDataUseCase(get())
    }

    viewModelOf(::PermissionViewModel)
    viewModel {
        CryptocurrencyViewModel(get())
    }
}

fun initKoin(
    appDeclaration: KoinAppDeclaration = {
    }
) = startKoin {
    appDeclaration()
    modules(commonModules)
}