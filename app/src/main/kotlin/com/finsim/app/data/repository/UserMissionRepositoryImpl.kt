package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.UserMissionProgressDao
import com.finsim.app.data.local.entity.UserMissionProgressEntity
import com.finsim.app.domain.model.UserMissionProgress
import com.finsim.app.domain.repository.UserMissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserMissionRepositoryImpl @Inject constructor(
    private val dao: UserMissionProgressDao,
) : UserMissionRepository {

    override fun getByProfileId(profileId: Long): Flow<List<UserMissionProgress>> =
        dao.getByProfileId(profileId).map { list -> list.map { it.toDomain() } }

    override suspend fun saveAll(missions: List<UserMissionProgress>) {
        dao.insertAll(missions.map { UserMissionProgressEntity.fromDomain(it) })
    }

    override suspend fun update(mission: UserMissionProgress) {
        dao.update(UserMissionProgressEntity.fromDomain(mission))
    }

    override suspend fun getByProfileAndMission(profileId: Long, missionId: String): UserMissionProgress? =
        dao.getByProfileAndMission(profileId, missionId)?.toDomain()
}
