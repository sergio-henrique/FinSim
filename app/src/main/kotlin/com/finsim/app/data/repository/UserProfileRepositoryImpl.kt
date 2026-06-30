package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.UserProfileDao
import com.finsim.app.data.local.entity.UserProfileEntity
import com.finsim.app.domain.model.AgeRange
import com.finsim.app.domain.model.UserProfile
import com.finsim.app.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao
) : UserProfileRepository {

    override suspend fun save(profile: UserProfile): Long =
        dao.insert(profile.toEntity())

    override suspend fun update(profile: UserProfile) =
        dao.update(profile.toEntity())

    override suspend fun getById(id: Long): UserProfile? =
        dao.getById(id)?.toDomain()

    override fun getAll(): Flow<List<UserProfile>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun deleteById(id: Long) = dao.deleteById(id)
}

// --- Mappers ---

private fun UserProfile.toEntity() = UserProfileEntity(
    id = id,
    name = name,
    ageRange = ageRange.name,
    monthlyIncome = monthlyIncome,
    currentMonth = currentMonth,
    createdAt = createdAt
)

private fun UserProfileEntity.toDomain() = UserProfile(
    id = id,
    name = name,
    ageRange = AgeRange.valueOf(ageRange),
    monthlyIncome = monthlyIncome,
    currentMonth = currentMonth,
    createdAt = createdAt
)
