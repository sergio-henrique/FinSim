package com.finsim.app.domain.repository

import com.finsim.app.domain.model.UserAchievementRecord
import kotlinx.coroutines.flow.Flow

interface UserAchievementRepository {
    fun getByProfileId(profileId: Long): Flow<List<UserAchievementRecord>>
    suspend fun save(achievement: UserAchievementRecord)
    suspend fun hasAchievement(profileId: Long, achievementId: String): Boolean
}
