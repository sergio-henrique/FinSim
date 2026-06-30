package com.finsim.app.domain.repository

import com.finsim.app.domain.model.StockHolding
import kotlinx.coroutines.flow.Flow

interface StockHoldingRepository {
    fun getByProfileId(profileId: Long): Flow<List<StockHolding>>
    suspend fun getByProfileAndTicker(profileId: Long, ticker: String): StockHolding?
    suspend fun save(holding: StockHolding)
    suspend fun update(holding: StockHolding)
    suspend fun delete(holding: StockHolding)
}
