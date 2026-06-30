package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.StockHoldingDao
import com.finsim.app.data.local.entity.StockHoldingEntity
import com.finsim.app.domain.model.StockHolding
import com.finsim.app.domain.repository.StockHoldingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StockHoldingRepositoryImpl @Inject constructor(
    private val dao: StockHoldingDao,
) : StockHoldingRepository {

    override fun getByProfileId(profileId: Long): Flow<List<StockHolding>> =
        dao.getByProfileId(profileId).map { list -> list.map { it.toDomain() } }

    override suspend fun getByProfileAndTicker(profileId: Long, ticker: String): StockHolding? =
        dao.getByProfileAndTicker(profileId, ticker)?.toDomain()

    override suspend fun save(holding: StockHolding) {
        dao.insert(StockHoldingEntity.fromDomain(holding))
    }

    override suspend fun update(holding: StockHolding) {
        dao.update(StockHoldingEntity.fromDomain(holding))
    }

    override suspend fun delete(holding: StockHolding) {
        dao.delete(StockHoldingEntity.fromDomain(holding))
    }
}
