package com.finsim.app.data.local.entity

import androidx.room.Entity
import com.finsim.app.domain.model.StockPriceHistory

@Entity(tableName = "stock_price_history", primaryKeys = ["ticker", "month"])
data class StockPriceHistoryEntity(
    val ticker: String,
    val month: Int,
    val priceCents: Long,
) {
    fun toDomain() = StockPriceHistory(ticker = ticker, month = month, priceCents = priceCents)

    companion object {
        fun fromDomain(d: StockPriceHistory) =
            StockPriceHistoryEntity(ticker = d.ticker, month = d.month, priceCents = d.priceCents)
    }
}
