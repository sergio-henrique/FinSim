package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finsim.app.data.local.entity.UserMissionProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserMissionProgressDao {

    @Query("SELECT * FROM user_mission_progress WHERE profileId = :profileId")
    fun getByProfileId(profileId: Long): Flow<List<UserMissionProgressEntity>>

    @Query("SELECT * FROM user_mission_progress WHERE profileId = :profileId AND missionId = :missionId LIMIT 1")
    suspend fun getByProfileAndMission(profileId: Long, missionId: String): UserMissionProgressEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(missions: List<UserMissionProgressEntity>)

    @Update
    suspend fun update(mission: UserMissionProgressEntity)
}
