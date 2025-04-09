package org.example.kmpsamples.infrastructure.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.example.kmpsamples.infrastructure.data.room.dtos.KlineStatisticViewDto

@Dao
interface KlineStatisticViewDao {

    @Query("SELECT * FROM KlineStatisticView")
    fun getStatistic(): Flow<List<KlineStatisticViewDto>>

}