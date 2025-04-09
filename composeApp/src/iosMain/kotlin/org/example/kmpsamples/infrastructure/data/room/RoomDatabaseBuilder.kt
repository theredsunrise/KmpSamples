package org.example.kmpsamples.infrastructure.data.room

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

inline fun <reified T : RoomDatabase> getDatabaseBuilder(): RoomDatabase.Builder<T> {
    val dbName = requireNotNull(T::class.simpleName)
    val dbFilePath = requireNotNull(documentDirectory().URLByAppendingPathComponent(dbName)?.path)
    return Room.databaseBuilder<T>(
        name = dbFilePath
    )
}

@OptIn(ExperimentalForeignApi::class)
inline fun documentDirectory(): NSURL {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory)
}