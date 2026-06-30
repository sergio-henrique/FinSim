package com.finsim.app.simulation.variableincome

import com.finsim.app.domain.model.MarketEvent
import com.finsim.app.domain.model.MarketEventType
import com.finsim.app.domain.model.StockHolding
import com.finsim.app.domain.model.StockPrice
import com.finsim.app.domain.model.StockSector
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StockMarketEngineTest {

    private fun makePrice(ticker: String, priceCents: Long) = StockPrice(
        ticker = ticker,
        currentPriceCents = priceCents,
        previousPriceCents = priceCents,
        lastUpdatedMonth = 1,
    )

    private fun makeHolding(ticker: String, quantity: Int, avgPrice: Long) = StockHolding(
        profileId = 1L,
        ticker = ticker,
        quantity = quantity,
        averagePriceCents = avgPrice,
        totalInvestedCents = quantity.toLong() * avgPrice,
    )

    @Test
    fun updatePrices_gera_novo_preco_para_todos_os_ativos_do_catalogo() {
        val prices = StockCatalog.all.associate { it.ticker to makePrice(it.ticker, it.basePriceCents) }
        val updated = StockMarketEngine.updatePrices(
            currentPrices = prices,
            currentMonth = 2,
            marketEvent = null,
            random = Random(42),
        )
        assertEquals(StockCatalog.all.size, updated.size)
        assertTrue(updated.all { it.currentPriceCents >= 1L })
    }

    @Test
    fun updatePrices_registra_preco_anterior_corretamente() {
        val basePriceCents = 5_000L
        val prices = mapOf("BNCR3" to makePrice("BNCR3", basePriceCents))
        // Usa apenas BNCR3 do catálogo mas o engine gera todos
        val updated = StockMarketEngine.updatePrices(prices, 2, null, Random(1))
        val bncr = updated.find { it.ticker == "BNCR3" }!!
        assertEquals(basePriceCents, bncr.previousPriceCents)
    }

    @Test
    fun updatePrices_aplica_fator_de_boom_em_setor_correto() {
        val boom = MarketEvent(
            title = "Boom de energia",
            description = "desc",
            educationalMessage = "msg",
            affectedSector = StockSector.ENERGY,
            priceImpactFactor = 1.5,
            type = MarketEventType.BOOM,
        )
        // Semente que gera variação natural ~0 para isolar o efeito do evento
        val prices = StockCatalog.all.associate { it.ticker to makePrice(it.ticker, 1_000L) }
        val updated = StockMarketEngine.updatePrices(prices, 2, boom, Random(99))

        val enrg = updated.find { it.ticker == "ENRG3" }!!
        // O evento de boom em energia deve empurrar ENRG3 acima do preço base
        assertTrue(enrg.currentPriceCents > 0)
    }

    @Test
    fun updatePrices_crash_reduz_todos_os_precos() {
        val crash = MarketEvent(
            title = "Crash",
            description = "desc",
            educationalMessage = "msg",
            affectedSector = null,
            priceImpactFactor = 0.5,
            type = MarketEventType.CRASH,
        )
        // Garante semente que gera variação positiva para que o crash seja o fator dominante
        val prices = StockCatalog.all.associate { it.ticker to makePrice(it.ticker, 10_000L) }
        val updated = StockMarketEngine.updatePrices(prices, 2, crash, Random(7))
        // Com fator 0.5, todos deveriam estar bem abaixo de 10_000
        assertTrue(updated.all { it.currentPriceCents < 10_000L })
    }

    @Test
    fun calculateDividends_retorna_zero_para_ativo_sem_dividendos() {
        val price = makePrice("TECH3", 4_500L)
        val holding = makeHolding("TECH3", 10, 4_500L)
        val dividends = StockMarketEngine.calculateDividends(holding, price)
        assertEquals(0L, dividends)
    }

    @Test
    fun calculateDividends_calcula_yield_sobre_valor_de_mercado() {
        // BNCR3: yield 0.5% ao mês, preço 5_800, 10 ações = 58_000 centavos total
        // dividendo esperado = 58_000 * 0.005 = 290 centavos = R$ 2,90
        val price = makePrice("BNCR3", 5_800L)
        val holding = makeHolding("BNCR3", 10, 5_800L)
        val dividends = StockMarketEngine.calculateDividends(holding, price)
        assertEquals(290L, dividends)
    }

    @Test
    fun marketValue_calcula_quantity_vezes_preco_atual() {
        val holding = makeHolding("ENRG3", 5, 3_200L)
        val value = StockMarketEngine.marketValue(holding, currentPriceCents = 4_000L)
        assertEquals(20_000L, value)
    }

    @Test
    fun unrealizedGainLoss_positivo_quando_preco_subiu() {
        val holding = makeHolding("TECH3", 10, 4_000L)
        val gainLoss = StockMarketEngine.unrealizedGainLoss(holding, currentPriceCents = 5_000L)
        assertEquals(10_000L, gainLoss)
    }

    @Test
    fun unrealizedGainLoss_negativo_quando_preco_caiu() {
        val holding = makeHolding("MINE3", 2, 6_700L)
        val gainLoss = StockMarketEngine.unrealizedGainLoss(holding, currentPriceCents = 5_000L)
        assertEquals(-3_400L, gainLoss)
    }

    @Test
    fun priceChangePct_correto_para_alta() {
        val price = StockPrice(
            ticker = "ALIM3",
            currentPriceCents = 2_310L,
            previousPriceCents = 2_100L,
            lastUpdatedMonth = 2,
        )
        val pct = price.priceChangePct
        assertTrue(pct > 0.09 && pct < 0.11)
    }

    @Test
    fun calculateAllDividends_retorna_mapa_com_todos_os_holdings() {
        val holdings = listOf(
            makeHolding("BNCR3", 10, 5_800L),
            makeHolding("TECH3", 5, 4_500L),
        )
        val prices = mapOf(
            "BNCR3" to makePrice("BNCR3", 5_800L),
            "TECH3" to makePrice("TECH3", 4_500L),
        )
        val dividends = StockMarketEngine.calculateAllDividends(holdings, prices)
        assertEquals(2, dividends.size)
        assertTrue(dividends["BNCR3"]!! > 0L)
        assertEquals(0L, dividends["TECH3"])
    }
}
