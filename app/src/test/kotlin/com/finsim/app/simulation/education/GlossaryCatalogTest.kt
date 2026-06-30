package com.finsim.app.simulation.education

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class GlossaryCatalogTest {

    @Test
    fun `catalogo tem termos`() {
        assert(GlossaryCatalog.all.isNotEmpty())
    }

    @Test
    fun `getById encontra termo existente`() {
        val term = GlossaryCatalog.getById("reserva_emergencia")
        assertNotNull(term)
        assertEquals("Reserva de Emergência", term!!.term)
    }

    @Test
    fun `getById retorna null para id inexistente`() {
        assertNull(GlossaryCatalog.getById("id_que_nao_existe"))
    }

    @Test
    fun `todos os termos tem campos nao vazios`() {
        GlossaryCatalog.all.forEach { term ->
            assert(term.id.isNotBlank()) { "id vazio: ${term.term}" }
            assert(term.term.isNotBlank()) { "term vazio: id=${term.id}" }
            assert(term.definition.isNotBlank()) { "definition vazio: ${term.term}" }
            assert(term.example.isNotBlank()) { "example vazio: ${term.term}" }
        }
    }

    @Test
    fun `ids sao unicos`() {
        val ids = GlossaryCatalog.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `glossario inclui termos essenciais`() {
        val ids = GlossaryCatalog.all.map { it.id }.toSet()
        listOf("reserva_emergencia", "tesouro_selic", "cdb", "inflacao", "dividendos", "risco").forEach {
            assert(it in ids) { "Termo essencial ausente: $it" }
        }
    }
}
