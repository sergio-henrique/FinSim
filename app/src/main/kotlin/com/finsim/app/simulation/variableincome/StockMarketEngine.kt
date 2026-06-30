package com.finsim.app.simulation.variableincome

import com.finsim.app.domain.model.MarketEvent
import com.finsim.app.domain.model.StockHolding
import com.finsim.app.domain.model.StockPrice
import kotlin.math.max
import kotlin.random.Random

/**
 * Motor de mercado de renda variável.
 *
 * Aplica variação de preços baseada em volatilidade por setor,
 * calcula dividendos mensais e aplica impacto de eventos de mercado.
 *
 * Todos os métodos são puros — sem efeitos colaterais ou acesso ao banco.
 *
 * Pedagogia:
 *   - Volatilidade diferente por setor ensina perfis de risco.
 *   - Dividendos mostram que ações podem gerar renda passiva.
 *   - Eventos de mercado demonstram que o preço de uma ação não depende só do usuário.
 */
object StockMarketEngine {

    private const val MIN_PRICE_CENTS = 100L

    /**
     * Atualiza os preços de todos os ativos para o novo mês.
     *
     * A variação de preço segue um random walk gaussiano limitado pela volatilidade
     * do ativo. Eventos de mercado aplicam um fator multiplicativo adicional.
     */
    fun updatePrices(
        currentPrices: Map<String, StockPrice>,
        currentMonth: Int,
        marketEvent: MarketEvent?,
        random: Random = Random,
    ): List<StockPrice> {
        return StockCatalog.all.map { asset ->
            val previous = currentPrices[asset.ticker]?.currentPriceCents ?: asset.basePriceCents

            val normalVariation = 1.0 + (random.nextGaussian() * asset.volatility)
            val eventFactor = when {
                marketEvent == null -> 1.0
                marketEvent.affectedSector == null -> marketEvent.priceImpactFactor
                marketEvent.affectedSector == asset.sector -> marketEvent.priceImpactFactor
                else -> 1.0
            }

            val newPrice = max(
                MIN_PRICE_CENTS,
                (previous * normalVariation * eventFactor).toLong(),
            )

            StockPrice(
                ticker = asset.ticker,
                currentPriceCents = newPrice,
                previousPriceCents = previous,
                lastUpdatedMonth = currentMonth,
            )
        }
    }

    /**
     * Calcula os dividendos devidos a um holder no mês atual.
     * Retorna o total em centavos.
     */
    fun calculateDividends(
        holding: StockHolding,
        currentPrice: StockPrice,
    ): Long {
        val asset = StockCatalog.getByTicker(holding.ticker) ?: return 0L
        if (asset.monthlyDividendYield <= 0.0) return 0L
        val totalValue = holding.quantity.toLong() * currentPrice.currentPriceCents
        return (totalValue * asset.monthlyDividendYield).toLong()
    }

    /**
     * Calcula os dividendos totais para todas as posições do usuário.
     * Retorna mapa de ticker → dividendos em centavos.
     */
    fun calculateAllDividends(
        holdings: List<StockHolding>,
        prices: Map<String, StockPrice>,
    ): Map<String, Long> =
        holdings.associate { holding ->
            val price = prices[holding.ticker] ?: return@associate holding.ticker to 0L
            holding.ticker to calculateDividends(holding, price)
        }

    /**
     * Calcula o valor de mercado atual de uma posição.
     */
    fun marketValue(holding: StockHolding, currentPriceCents: Long): Long =
        holding.quantity.toLong() * currentPriceCents

    /**
     * Calcula o lucro/prejuízo não realizado de uma posição.
     */
    fun unrealizedGainLoss(holding: StockHolding, currentPriceCents: Long): Long =
        marketValue(holding, currentPriceCents) - holding.totalInvestedCents
}

private fun Random.nextGaussian(): Double {
    var u: Double
    var v: Double
    var s: Double
    do {
        u = nextDouble() * 2 - 1
        v = nextDouble() * 2 - 1
        s = u * u + v * v
    } while (s >= 1 || s == 0.0)
    return u * Math.sqrt(-2.0 * Math.log(s) / s)
}
