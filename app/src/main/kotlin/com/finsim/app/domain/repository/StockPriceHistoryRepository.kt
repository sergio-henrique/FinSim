package com.finsim.app.domain.repository

import com.finsim.app.domain.model.StockPriceHistory

interface StockPriceHistoryRepository {
    suspend fun getByTicker(ticker: String): List<StockPriceHistory>
    suspend fun saveAll(entries: List<StockPriceHistory>)
}
