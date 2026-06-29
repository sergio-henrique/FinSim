package com.finsim.app.domain.repository

import com.finsim.app.domain.model.MonthlySnapshot
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso aos snapshots mensais de patrimônio.
 */
interface MonthlySnapshotRepository {
    suspend fun save(snapshot: MonthlySnapshot): Long
    suspend fun getByProfileIdAndMonth(profileId: Long, month: Int): MonthlySnapshot?
    fun getAllByProfileId(profileId: Long): Flow<List<MonthlySnapshot>>
}
