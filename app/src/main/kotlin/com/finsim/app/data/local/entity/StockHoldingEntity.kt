package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finsim.app.domain.model.StockHolding

@Entity(tableName = "stock_holdings")
data class StockHoldingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val ticker: String,
    val quantity: Int,
    val averagePriceCents: Long,
    val totalInvestedCents: Long,
) {
    fun toDomain() = StockHolding(
        id = id,
        profileId = profileId,
        ticker = ticker,
        quantity = quantity,
        averagePriceCents = averagePriceCents,
        totalInvestedCents = totalInvestedCents,
    )

    companion object {
        fun fromDomain(d: StockHolding) = StockHoldingEntity(
            id = d.id,
            profileId = d.profileId,
            ticker = d.ticker,
            quantity = d.quantity,
            averagePriceCents = d.averagePriceCents,
            totalInvestedCents = d.totalInvestedCents,
        )
    }
}
