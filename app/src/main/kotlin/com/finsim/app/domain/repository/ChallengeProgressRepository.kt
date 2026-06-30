package com.finsim.app.domain.repository

import com.finsim.app.domain.model.ChallengeProgress
import kotlinx.coroutines.flow.Flow

interface ChallengeProgressRepository {
    fun getByProfileId(profileId: Long): Flow<List<ChallengeProgress>>
    suspend fun getActiveByProfileId(profileId: Long): List<ChallengeProgress>
    suspend fun save(progress: ChallengeProgress): Long
    suspend fun update(progress: ChallengeProgress)
}
