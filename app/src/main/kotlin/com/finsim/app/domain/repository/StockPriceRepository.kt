package com.finsim.app.domain.repository

import com.finsim.app.domain.model.StockPrice
import kotlinx.coroutines.flow.Flow

interface StockPriceRepository {
    fun getAll(): Flow<List<StockPrice>>
    suspend fun getByTicker(ticker: String): StockPrice?
    suspend fun save(price: StockPrice)
    suspend fun update(price: StockPrice)
    suspend fun upsert(price: StockPrice)
}
