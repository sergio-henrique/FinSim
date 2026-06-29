package com.finsim.app.simulation.economy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InflationEngineTest {

    @Test
    fun `applyMonthly deve aumentar o valor em zero por cento sem alteração`() {
        val result = InflationEngine.applyMonthly(10_000L, 0.0)
        assertEquals(10_000L, result)
    }

    @Test
    fun `applyMonthly deve aumentar o valor com taxa de 0,4 por cento`() {
        // 10_000 * 1.004 = 10_040
        val result = InflationEngine.applyMonthly(10_000L, 0.004)
        assertEquals(10_040L, result)
    }

    @Test
    fun `applyMonthly deve arredondar para cima em caso fracionário`() {
        // 10_001 * 1.004 = 10_041.004 → arredonda para 10_041
        val result = InflationEngine.applyMonthly(10_001L, 0.004)
        assertTrue(result >= 10_041L)
    }

    @Test
    fun `applyMonthly nunca deve retornar valor menor que o original com taxa positiva`() {
        val original = 50_000L
        val result = InflationEngine.applyMonthly(original, 0.004)
        assertTrue(result >= original)
    }

    @Test
    fun `accumulatedLoss com zero meses deve retornar zero`() {
        val loss = InflationEngine.accumulatedLoss(100_000L, 0.004, 0)
        assertEquals(0L, loss)
    }

    @Test
    fun `accumulatedLoss deve crescer com número de meses`() {
        val loss6 = InflationEngine.accumulatedLoss(100_000L, 0.004, 6)
        val loss12 = InflationEngine.accumulatedLoss(100_000L, 0.004, 12)
        assertTrue(loss12 > loss6)
    }

    @Test
    fun `realReturn deve ser menor que a taxa nominal`() {
        val real = InflationEngine.realReturn(nominalRate = 0.009, inflationRate = 0.004)
        assertTrue(real < 0.009)
        assertTrue(real > 0.0)
    }

    @Test
    fun `realReturn com inflação igual à taxa nominal deve resultar em retorno próximo de zero`() {
        val real = InflationEngine.realReturn(nominalRate = 0.004, inflationRate = 0.004)
        assertTrue(real < 0.001)
    }
}
