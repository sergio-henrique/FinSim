package com.finsim.app.domain.repository

import com.finsim.app.domain.model.UserMissionProgress
import kotlinx.coroutines.flow.Flow

interface UserMissionRepository {
    fun getByProfileId(profileId: Long): Flow<List<UserMissionProgress>>
    suspend fun saveAll(missions: List<UserMissionProgress>)
    suspend fun update(mission: UserMissionProgress)
    suspend fun getByProfileAndMission(profileId: Long, missionId: String): UserMissionProgress?
}
