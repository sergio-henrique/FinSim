package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finsim.app.domain.model.StockPrice

@Entity(tableName = "stock_prices")
data class StockPriceEntity(
    @PrimaryKey val ticker: String,
    val currentPriceCents: Long,
    val previousPriceCents: Long,
    val lastUpdatedMonth: Int,
) {
    fun toDomain() = StockPrice(
        ticker = ticker,
        currentPriceCents = currentPriceCents,
        previousPriceCents = previousPriceCents,
        lastUpdatedMonth = lastUpdatedMonth,
    )

    companion object {
        fun fromDomain(d: StockPrice) = StockPriceEntity(
            ticker = d.ticker,
            currentPriceCents = d.currentPriceCents,
            previousPriceCents = d.previousPriceCents,
            lastUpdatedMonth = d.lastUpdatedMonth,
        )
    }
}
