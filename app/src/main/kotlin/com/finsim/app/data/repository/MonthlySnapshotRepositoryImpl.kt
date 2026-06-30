package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.MonthlySnapshotDao
import com.finsim.app.data.local.entity.MonthlySnapshotEntity
import com.finsim.app.domain.model.MonthlySnapshot
import com.finsim.app.domain.repository.MonthlySnapshotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MonthlySnapshotRepositoryImpl @Inject constructor(
    private val dao: MonthlySnapshotDao
) : MonthlySnapshotRepository {

    override suspend fun save(snapshot: MonthlySnapshot): Long =
        dao.insert(snapshot.toEntity())

    override suspend fun getByProfileIdAndMonth(profileId: Long, month: Int): MonthlySnapshot? =
        dao.getByProfileIdAndMonth(profileId, month)?.toDomain()

    override suspend fun getLatestByProfileId(profileId: Long): MonthlySnapshot? =
        dao.getLatestByProfileId(profileId)?.toDomain()

    override fun getAllByProfileId(profileId: Long): Flow<List<MonthlySnapshot>> =
        dao.getAllByProfileId(profileId).map { list -> list.map { it.toDomain() } }
}

// --- Mappers ---

private fun MonthlySnapshot.toEntity() = MonthlySnapshotEntity(
    id = id,
    profileId = profileId,
    month = month,
    accountBalance = accountBalance,
    reserveBalance = reserveBalance,
    fixedIncomeBalance = fixedIncomeBalance,
    totalWealth = totalWealth,
    billsPaidAmount = billsPaidAmount,
    billsPendingAmount = billsPendingAmount,
    financialHealthScore = financialHealthScore
)

private fun MonthlySnapshotEntity.toDomain() = MonthlySnapshot(
    id = id,
    profileId = profileId,
    month = month,
    accountBalance = accountBalance,
    reserveBalance = reserveBalance,
    fixedIncomeBalance = fixedIncomeBalance,
    totalWealth = totalWealth,
    billsPaidAmount = billsPaidAmount,
    billsPendingAmount = billsPendingAmount,
    financialHealthScore = financialHealthScore
)
