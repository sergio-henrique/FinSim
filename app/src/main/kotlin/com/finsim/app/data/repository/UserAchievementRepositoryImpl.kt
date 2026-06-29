package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.UserAchievementRecordDao
import com.finsim.app.data.local.entity.UserAchievementRecordEntity
import com.finsim.app.domain.model.UserAchievementRecord
import com.finsim.app.domain.repository.UserAchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserAchievementRepositoryImpl @Inject constructor(
    private val dao: UserAchievementRecordDao,
) : UserAchievementRepository {

    override fun getByProfileId(profileId: Long): Flow<List<UserAchievementRecord>> =
        dao.getByProfileId(profileId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(achievement: UserAchievementRecord) {
        dao.insert(UserAchievementRecordEntity.fromDomain(achievement))
    }

    override suspend fun hasAchievement(profileId: Long, achievementId: String): Boolean =
        dao.countByProfileAndAchievement(profileId, achievementId) > 0
}
