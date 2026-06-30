package com.finsim.app.application.usecase

import com.finsim.app.domain.model.StockPriceHistory
import com.finsim.app.domain.repository.StockPriceHistoryRepository
import javax.inject.Inject

class GetStockPriceHistoryUseCase @Inject constructor(
    private val repository: StockPriceHistoryRepository,
) {
    suspend operator fun invoke(ticker: String): List<StockPriceHistory> =
        repository.getByTicker(ticker)
}
