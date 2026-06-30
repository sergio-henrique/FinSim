package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.StockPriceDao
import com.finsim.app.data.local.entity.StockPriceEntity
import com.finsim.app.domain.model.StockPrice
import com.finsim.app.domain.repository.StockPriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StockPriceRepositoryImpl @Inject constructor(
    private val dao: StockPriceDao,
) : StockPriceRepository {

    override fun getAll(): Flow<List<StockPrice>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getByTicker(ticker: String): StockPrice? =
        dao.getByTicker(ticker)?.toDomain()

    override suspend fun save(price: StockPrice) = dao.upsert(StockPriceEntity.fromDomain(price))

    override suspend fun update(price: StockPrice) = dao.upsert(StockPriceEntity.fromDomain(price))

    override suspend fun upsert(price: StockPrice) = dao.upsert(StockPriceEntity.fromDomain(price))
}
