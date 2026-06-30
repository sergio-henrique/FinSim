package com.finsim.app.domain.model

/**
 * Definição imutável de um ativo de renda variável simulado.
 * Não é persistida — vive no [StockCatalog].
 *
 * Critério pedagógico: cada ativo representa um setor real da economia
 * com características distintas de risco/retorno para que o usuário
 * aprenda diversificação.
 */
data class StockAsset(
    val ticker: String,
    val name: String,
    val sector: StockSector,
    val basePriceCents: Long,
    val volatility: Double,
    val monthlyDividendYield: Double,
    val description: String,
)

enum class StockSector {
    ENERGY, FINANCE, TECHNOLOGY, CONSUMER_GOODS, MINING
}

/**
 * Preço atual de mercado de um ativo. Persistido e atualizado a cada mês.
 */
data class StockPrice(
    val id: Long = 0,
    val ticker: String,
    val currentPriceCents: Long,
    val previousPriceCents: Long,
    val lastUpdatedMonth: Int,
) {
    val priceChangePct: Double
        get() = if (previousPriceCents > 0)
            (currentPriceCents - previousPriceCents).toDouble() / previousPriceCents
        else 0.0
}

/**
 * Posição do usuário em um ativo. Persistida.
 */
data class StockHolding(
    val id: Long = 0,
    val profileId: Long,
    val ticker: String,
    val quantity: Int,
    val averagePriceCents: Long,
    val totalInvestedCents: Long,
) {
    val currentValueCents: Long get() = quantity * averagePriceCents
}

/**
 * Evento de mercado que afeta um setor ou todo o mercado.
 */
data class MarketEvent(
    val title: String,
    val description: String,
    val educationalMessage: String,
    val affectedSector: StockSector?,
    val priceImpactFactor: Double,
    val type: MarketEventType,
)

enum class MarketEventType { BOOM, BUST, CRASH, RECOVERY }
