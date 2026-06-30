package com.finsim.app.application.usecase

import com.finsim.app.domain.model.StockAsset
import com.finsim.app.domain.model.StockHolding
import com.finsim.app.domain.model.StockPrice
import com.finsim.app.domain.repository.StockHoldingRepository
import com.finsim.app.domain.repository.StockPriceRepository
import com.finsim.app.simulation.variableincome.StockCatalog
import com.finsim.app.simulation.variableincome.StockMarketEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class PortfolioItem(
    val asset: StockAsset,
    val holding: StockHolding?,
    val price: StockPrice?,
    val marketValueCents: Long,
    val unrealizedGainLoss: Long,
)

data class Portfolio(
    val items: List<PortfolioItem>,
    val totalMarketValueCents: Long,
    val totalInvestedCents: Long,
    val totalUnrealizedGainLoss: Long,
)

class GetPortfolioUseCase @Inject constructor(
    private val stockHoldingRepository: StockHoldingRepository,
    private val stockPriceRepository: StockPriceRepository,
) {
    operator fun invoke(profileId: Long): Flow<Portfolio> =
        combine(
            stockHoldingRepository.getByProfileId(profileId),
            stockPriceRepository.getAll(),
        ) { holdings, prices ->
            val holdingMap = holdings.associateBy { it.ticker }
            val priceMap = prices.associateBy { it.ticker }

            val items = StockCatalog.all.map { asset ->
                val holding = holdingMap[asset.ticker]
                val price = priceMap[asset.ticker]
                val currentPrice = price?.currentPriceCents ?: asset.basePriceCents

                val marketValue = holding?.let {
                    StockMarketEngine.marketValue(it, currentPrice)
                } ?: 0L

                val gainLoss = holding?.let {
                    StockMarketEngine.unrealizedGainLoss(it, currentPrice)
                } ?: 0L

                PortfolioItem(
                    asset = asset,
                    holding = holding,
                    price = price ?: StockPrice(
                        ticker = asset.ticker,
                        currentPriceCents = asset.basePriceCents,
                        previousPriceCents = asset.basePriceCents,
                        lastUpdatedMonth = 0,
                    ),
                    marketValueCents = marketValue,
                    unrealizedGainLoss = gainLoss,
                )
            }

            val myItems = items.filter { it.holding != null }
            Portfolio(
                items = items,
                totalMarketValueCents = myItems.sumOf { it.marketValueCents },
                totalInvestedCents = myItems.sumOf { it.holding!!.totalInvestedCents },
                totalUnrealizedGainLoss = myItems.sumOf { it.unrealizedGainLoss },
            )
        }
}
