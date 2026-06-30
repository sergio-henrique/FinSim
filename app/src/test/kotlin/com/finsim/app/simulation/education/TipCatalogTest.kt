package com.finsim.app.simulation.education

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TipCatalogTest {

    @Test
    fun `catalogo tem ao menos uma dica`() {
        assert(TipCatalog.all.isNotEmpty())
    }

    @Test
    fun `forMonth mes 1 retorna primeira dica`() {
        val tip = TipCatalog.forMonth(1)
        assertEquals(TipCatalog.all[0], tip)
    }

    @Test
    fun `forMonth rotaciona corretamente apos esgotar todas as dicas`() {
        val size = TipCatalog.all.size
        val tipA = TipCatalog.forMonth(1)
        val tipB = TipCatalog.forMonth(1 + size)
        assertEquals(tipA, tipB)
    }

    @Test
    fun `forMonth para mes igual ao tamanho do catalogo retorna ultimo item`() {
        val size = TipCatalog.all.size
        val tip = TipCatalog.forMonth(size)
        assertEquals(TipCatalog.all[size - 1], tip)
    }

    @Test
    fun `forMonth para mes muito alto nao lanca excecao`() {
        assertNotNull(TipCatalog.forMonth(999))
    }

    @Test
    fun `todas as dicas tem campos nao vazios`() {
        TipCatalog.all.forEach { tip ->
            assert(tip.id.isNotBlank()) { "id vazio: $tip" }
            assert(tip.title.isNotBlank()) { "title vazio: $tip" }
            assert(tip.body.isNotBlank()) { "body vazio: $tip" }
            assert(tip.emoji.isNotBlank()) { "emoji vazio: $tip" }
        }
    }
}
