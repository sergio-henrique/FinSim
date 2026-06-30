package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finsim.app.data.local.entity.ChallengeProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeProgressDao {

    @Query("SELECT * FROM challenge_progress WHERE profileId = :profileId")
    fun getByProfileId(profileId: Long): Flow<List<ChallengeProgressEntity>>

    @Query("SELECT * FROM challenge_progress WHERE profileId = :profileId AND status = 'ACTIVE'")
    suspend fun getActiveByProfileId(profileId: Long): List<ChallengeProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChallengeProgressEntity): Long

    @Update
    suspend fun update(entity: ChallengeProgressEntity)
}
