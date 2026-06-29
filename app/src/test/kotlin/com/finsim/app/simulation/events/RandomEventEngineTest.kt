package com.finsim.app.simulation.events

import com.finsim.app.domain.model.RandomEvent
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RandomEventEngineTest {

    @Test
    fun `evento gerado deve ter titulo e mensagem nao vazios`() {
        var event: RandomEvent? = null
        for (seed in 0..100L) {
            event = RandomEventEngine.generate(month = 1, random = Random(seed))
            if (event != null) break
        }
        assertNotNull(event, "Não foi possível gerar evento em 100 tentativas")
        assertTrue(event.title.isNotBlank())
        assertTrue(event.educationalMessage.isNotBlank())
    }

    @Test
    fun `evento gerado deve ter amountCents positivo`() {
        var event: RandomEvent? = null
        for (seed in 0..100L) {
            event = RandomEventEngine.generate(month = 3, random = Random(seed))
            if (event != null) break
        }
        assertNotNull(event, "Não foi possível gerar evento em 100 tentativas")
        assertTrue(event.amountCents > 0)
    }

    @Test
    fun `aproximadamente 75 por cento dos meses nao devem gerar evento`() {
        var nullCount = 0
        repeat(1000) {
            if (RandomEventEngine.generate(month = 1) == null) nullCount++
        }
        assertTrue(nullCount in 600..900,
            "Taxa de não-eventos fora do intervalo esperado: $nullCount/1000")
    }

    @Test
    fun `valor do evento no mes 12 deve ser maior ou igual ao mes 1 com mesmo seed`() {
        val seed = 5L
        val eventMonth1 = RandomEventEngine.generate(month = 1, random = Random(seed))
        val eventMonth12 = RandomEventEngine.generate(month = 12, random = Random(seed))

        if (eventMonth1 != null && eventMonth12 != null) {
            assertTrue(eventMonth12.amountCents >= eventMonth1.amountCents,
                "Evento mês 12 deveria ser >= mês 1: ${eventMonth12.amountCents} vs ${eventMonth1.amountCents}")
        }
    }

    @Test
    fun `valor do evento nao deve ultrapassar 2x o valor base mesmo no mes 100`() {
        var event: RandomEvent? = null
        for (seed in 0..100L) {
            event = RandomEventEngine.generate(month = 100, random = Random(seed))
            if (event != null) break
        }
        assertNotNull(event, "Não foi possível gerar evento em 100 tentativas")
        assertTrue(event.amountCents <= 60_000L,
            "Valor do evento não deveria ultrapassar 2x o base: ${event.amountCents}")
    }
}
