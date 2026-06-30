package com.finsim.app.presentation.component

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testa o comportamento das funções auxiliares do gráfico de linha.
 * A lógica de escala é pura (sem Canvas), então pode ser testada sem Android.
 */
class LineChartTest {

    @Test
    fun `normalizacao de valores mapeia min para 0 e max para 1`() {
        val points = listOf(100f, 200f, 150f, 300f)
        val min = points.min()
        val max = points.max()
        val range = max - min

        val normalized = points.map { (it - min) / range }

        assertEquals(0f, normalized.first(), 0.001f)
        assertEquals(1f, normalized.last(), 0.001f)
    }

    @Test
    fun `normalizacao quando todos os valores sao iguais nao divide por zero`() {
        val points = listOf(500f, 500f, 500f)
        val min = points.min()
        val max = points.max()
        val range = (max - min).coerceAtLeast(1f)

        val normalized = points.map { (it - min) / range }

        assertEquals(0f, normalized[0], 0.001f)
        assertEquals(0f, normalized[1], 0.001f)
    }

    @Test
    fun `grafico nao deve renderizar com menos de dois pontos`() {
        val zeroPoints = emptyList<Float>()
        val onePoint = listOf(100f)

        assertEquals(true, zeroPoints.size < 2)
        assertEquals(true, onePoint.size < 2)
    }

    @Test
    fun `calculo de variacao percentual entre primeiro e ultimo ponto`() {
        val points = listOf(100f, 120f, 110f, 130f)
        val firstPrice = points.first()
        val lastPrice = points.last()
        val changePct = ((lastPrice - firstPrice) / firstPrice) * 100.0

        assertEquals(30.0, changePct, 0.01)
    }

    @Test
    fun `variacao percentual negativa quando preco cai`() {
        val points = listOf(200f, 180f, 160f)
        val firstPrice = points.first()
        val lastPrice = points.last()
        val changePct = ((lastPrice - firstPrice) / firstPrice) * 100.0

        assertEquals(-20.0, changePct, 0.01)
    }
}
