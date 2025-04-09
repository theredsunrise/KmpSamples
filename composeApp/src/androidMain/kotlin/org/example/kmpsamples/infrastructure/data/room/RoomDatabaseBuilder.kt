package org.example.kmpsamples.infrastructure.data.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

inline fun <reified T : RoomDatabase> getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<T> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(T::class.simpleName)
    return Room.databaseBuilder<T>(
        context = appContext,
        name = dbFile.absolutePath
    )
}