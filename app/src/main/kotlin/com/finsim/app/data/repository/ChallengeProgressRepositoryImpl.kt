package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.ChallengeProgressDao
import com.finsim.app.data.local.entity.ChallengeProgressEntity
import com.finsim.app.domain.model.ChallengeProgress
import com.finsim.app.domain.repository.ChallengeProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChallengeProgressRepositoryImpl @Inject constructor(
    private val dao: ChallengeProgressDao,
) : ChallengeProgressRepository {

    override fun getByProfileId(profileId: Long): Flow<List<ChallengeProgress>> =
        dao.getByProfileId(profileId).map { list -> list.map { it.toDomain() } }

    override suspend fun getActiveByProfileId(profileId: Long): List<ChallengeProgress> =
        dao.getActiveByProfileId(profileId).map { it.toDomain() }

    override suspend fun save(progress: ChallengeProgress): Long =
        dao.insert(ChallengeProgressEntity.fromDomain(progress))

    override suspend fun update(progress: ChallengeProgress) =
        dao.update(ChallengeProgressEntity.fromDomain(progress))
}
