package com.finsim.app.simulation.variableincome

import com.finsim.app.domain.model.StockAsset
import com.finsim.app.domain.model.StockSector

/**
 * Catálogo de ações fictícias do FinSim.
 *
 * Cada ativo representa um setor real com características distintas
 * de risco e retorno, para que o usuário aprenda diversificação de carteira.
 *
 * Os tickers terminam em "3" (ação ordinária) por convenção pedagógica,
 * sem nenhuma relação com ativos reais da bolsa brasileira.
 */
object StockCatalog {

    val ENRG3 = StockAsset(
        ticker = "ENRG3",
        name = "EnergiaBras",
        sector = StockSector.ENERGY,
        basePriceCents = 3_200L,
        volatility = 0.08,
        monthlyDividendYield = 0.006,
        description = "Empresa de energia elétrica. Paga dividendos regulares e tem volatilidade moderada. Boa para quem quer renda passiva estável.",
    )

    val BNCR3 = StockAsset(
        ticker = "BNCR3",
        name = "BancoPrime",
        sector = StockSector.FINANCE,
        basePriceCents = 5_800L,
        volatility = 0.05,
        monthlyDividendYield = 0.005,
        description = "Banco tradicional. Baixa volatilidade e dividendos regulares. Indicado para perfis conservadores que querem previsibilidade.",
    )

    val TECH3 = StockAsset(
        ticker = "TECH3",
        name = "TecBrazil",
        sector = StockSector.TECHNOLOGY,
        basePriceCents = 4_500L,
        volatility = 0.18,
        monthlyDividendYield = 0.0,
        description = "Empresa de tecnologia em crescimento. Alta volatilidade e sem dividendos. Pode valorizar muito — ou perder valor rapidamente.",
    )

    val ALIM3 = StockAsset(
        ticker = "ALIM3",
        name = "AlimentaBr",
        sector = StockSector.CONSUMER_GOODS,
        basePriceCents = 2_100L,
        volatility = 0.06,
        monthlyDividendYield = 0.004,
        description = "Empresa de alimentos e consumo básico. As pessoas sempre precisam comer — isso reduz o risco do setor em crises.",
    )

    val MINE3 = StockAsset(
        ticker = "MINE3",
        name = "MinéraRio",
        sector = StockSector.MINING,
        basePriceCents = 6_700L,
        volatility = 0.15,
        monthlyDividendYield = 0.007,
        description = "Mineradora de grande porte. Alta volatilidade ligada ao preço das commodities globais. Paga dividendos bons, mas com risco elevado.",
    )

    val all: List<StockAsset> = listOf(ENRG3, BNCR3, TECH3, ALIM3, MINE3)

    fun getByTicker(ticker: String): StockAsset? = all.firstOrNull { it.ticker == ticker }
}
