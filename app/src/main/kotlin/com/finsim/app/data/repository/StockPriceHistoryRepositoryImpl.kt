package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.StockPriceHistoryDao
import com.finsim.app.data.local.entity.StockPriceHistoryEntity
import com.finsim.app.domain.model.StockPriceHistory
import com.finsim.app.domain.repository.StockPriceHistoryRepository
import javax.inject.Inject

class StockPriceHistoryRepositoryImpl @Inject constructor(
    private val dao: StockPriceHistoryDao,
) : StockPriceHistoryRepository {
    override suspend fun getByTicker(ticker: String): List<StockPriceHistory> =
        dao.getByTicker(ticker).map { it.toDomain() }

    override suspend fun saveAll(entries: List<StockPriceHistory>) =
        dao.insertAll(entries.map { StockPriceHistoryEntity.fromDomain(it) })
}
