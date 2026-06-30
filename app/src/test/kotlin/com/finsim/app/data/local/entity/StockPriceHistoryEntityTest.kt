package com.finsim.app.data.local.entity

import com.finsim.app.domain.model.StockPriceHistory
import org.junit.Assert.assertEquals
import org.junit.Test

class StockPriceHistoryEntityTest {

    @Test
    fun `fromDomain e toDomain sao inversos`() {
        val domain = StockPriceHistory(ticker = "TECH3", month = 5, priceCents = 4_800L)
        val entity = StockPriceHistoryEntity.fromDomain(domain)
        val restored = entity.toDomain()

        assertEquals(domain, restored)
    }

    @Test
    fun `entity preserva ticker e month como chave composta`() {
        val entity = StockPriceHistoryEntity(ticker = "ENRG3", month = 12, priceCents = 3_400L)
        assertEquals("ENRG3", entity.ticker)
        assertEquals(12, entity.month)
        assertEquals(3_400L, entity.priceCents)
    }

    @Test
    fun `diferentes tickers no mesmo mes geram entidades distintas`() {
        val e1 = StockPriceHistoryEntity(ticker = "BNCR3", month = 3, priceCents = 5_600L)
        val e2 = StockPriceHistoryEntity(ticker = "MINE3", month = 3, priceCents = 7_100L)

        assertEquals(false, e1 == e2)
        assertEquals(e1.month, e2.month)
    }
}
