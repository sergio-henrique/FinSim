package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finsim.app.data.local.entity.MonthlySnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlySnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MonthlySnapshotEntity): Long

    @Query(
        """
        SELECT * FROM monthly_snapshots
        WHERE profileId = :profileId AND month = :month
        LIMIT 1
        """
    )
    suspend fun getByProfileIdAndMonth(profileId: Long, month: Int): MonthlySnapshotEntity?

    @Query("SELECT * FROM monthly_snapshots WHERE profileId = :profileId ORDER BY month ASC")
    fun getAllByProfileId(profileId: Long): Flow<List<MonthlySnapshotEntity>>
}
