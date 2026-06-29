package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finsim.app.data.local.entity.UserAchievementRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAchievementRecordDao {

    @Query("SELECT * FROM user_achievement_records WHERE profileId = :profileId ORDER BY unlockedAt ASC")
    fun getByProfileId(profileId: Long): Flow<List<UserAchievementRecordEntity>>

    @Query("SELECT COUNT(*) FROM user_achievement_records WHERE profileId = :profileId AND achievementId = :achievementId")
    suspend fun countByProfileAndAchievement(profileId: Long, achievementId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(achievement: UserAchievementRecordEntity)
}
