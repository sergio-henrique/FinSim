package com.finsim.app.simulation.fixedincome

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testes unitarios do FixedIncomeEngine.
 *
 * Cada teste cobre uma regra de negocio isolada. Os nomes descrevem o
 * comportamento esperado, facilitando a revisao pedagogica.
 *
 * Convencao: `metodo_cenario_resultadoEsperado`
 */
class FixedIncomeEngineTest {

    // -------------------------------------------------------------------------
    // applyMonthlyReturn
    // -------------------------------------------------------------------------

    @Test
    fun applyMonthlyReturn_valorZero_retornaZero() {
        // Nenhum dinheiro investido nao gera rendimento
        val result = FixedIncomeEngine.applyMonthlyReturn(
            currentAmountCents = 0L,
            monthlyRate = 0.008
        )
        assertEquals(0L, result)
    }

    @Test
    fun applyMonthlyReturn_taxaZero_retornaMesmoValor() {
        // Taxa zero significa sem rendimento
        val result = FixedIncomeEngine.applyMonthlyReturn(
            currentAmountCents = 100_000L,
            monthlyRate = 0.0
        )
        assertEquals(100_000L, result)
    }

    @Test
    fun applyMonthlyReturn_valorNormalTaxa08_retornaValorCorreto() {
        // R$ 1.000,00 a 0,8%/mes: 100_000 * 1.008 = 100_800 centavos
        val result = FixedIncomeEngine.applyMonthlyReturn(
            currentAmountCents = 100_000L,
            monthlyRate = 0.008
        )
        assertEquals(100_800L, result)
    }

    @Test
    fun applyMonthlyReturn_valorPequeno_truncaCentavosFracionarios() {
        // 1_000 * 1.008 = 1_008.0 sem fracao
        val result = FixedIncomeEngine.applyMonthlyReturn(
            currentAmountCents = 1_000L,
            monthlyRate = 0.008
        )
        assertEquals(1_008L, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun applyMonthlyReturn_valorNegativo_lancaIllegalArgumentException() {
        FixedIncomeEngine.applyMonthlyReturn(
            currentAmountCents = -1L,
            monthlyRate = 0.008
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun applyMonthlyReturn_taxaNegativa_lancaIllegalArgumentException() {
        FixedIncomeEngine.applyMonthlyReturn(
            currentAmountCents = 100_000L,
            monthlyRate = -0.001
        )
    }

    // -------------------------------------------------------------------------
    // applyCompoundReturn
    // -------------------------------------------------------------------------

    @Test
    fun applyCompoundReturn_zeroMeses_retornaValorInicial() {
        // (1 + taxa)^0 = 1, portanto resultado = valor_inicial
        val result = FixedIncomeEngine.applyCompoundReturn(
            initialAmountCents = 100_000L,
            monthlyRate = 0.008,
            months = 0
        )
        assertEquals(100_000L, result)
    }

    @Test
    fun applyCompoundReturn_umMes_equivaleAapplyMonthlyReturn() {
        // Compostos por 1 mes deve ser identico ao rendimento simples mensal
        val compoundResult = FixedIncomeEngine.applyCompoundReturn(
            initialAmountCents = 100_000L,
            monthlyRate = 0.008,
            months = 1
        )
        val simpleResult = FixedIncomeEngine.applyMonthlyReturn(
            currentAmountCents = 100_000L,
            monthlyRate = 0.008
        )
        assertEquals(simpleResult, compoundResult)
    }

    @Test
    fun applyCompoundReturn_12Meses_maiorQueJurosSimples() {
        // Prova do efeito composto: 12 * 0,8% = 9,6% simples
        // mas (1.008)^12 - 1 aprox 10,03% compostos
        val initialAmount = 100_000L
        val monthlyRate = 0.008

        val compoundResult = FixedIncomeEngine.applyCompoundReturn(
            initialAmountCents = initialAmount,
            monthlyRate = monthlyRate,
            months = 12
        )
        val simpleInterest = initialAmount + (12 * (initialAmount * monthlyRate)).toLong()

        assertTrue(
            "Juros compostos ($compoundResult) devem ser maiores que simples ($simpleInterest)",
            compoundResult > simpleInterest
        )
    }

    @Test
    fun applyCompoundReturn_12Meses_retornaAproximadamente110034() {
        // R$ 1.000,00 a 0,8%/mes por 12 meses
        // 100_000 * (1.008)^12 aprox 110_034 centavos
        val result = FixedIncomeEngine.applyCompoundReturn(
            initialAmountCents = 100_000L,
            monthlyRate = 0.008,
            months = 12
        )
        assertTrue(
            "Resultado esperado ~110034, obtido $result",
            result in 110_033L..110_035L
        )
    }

    @Test
    fun applyCompoundReturn_valorZero_retornaZeroIndependenteDoPeriodo() {
        // Sem capital inicial nenhum rendimento e possivel
        val result = FixedIncomeEngine.applyCompoundReturn(
            initialAmountCents = 0L,
            monthlyRate = 0.008,
            months = 24
        )
        assertEquals(0L, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun applyCompoundReturn_valorNegativo_lancaIllegalArgumentException() {
        FixedIncomeEngine.applyCompoundReturn(
            initialAmountCents = -100L,
            monthlyRate = 0.008,
            months = 12
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun applyCompoundReturn_taxaNegativa_lancaIllegalArgumentException() {
        FixedIncomeEngine.applyCompoundReturn(
            initialAmountCents = 100_000L,
            monthlyRate = -0.001,
            months = 12
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun applyCompoundReturn_mesesNegativos_lancaIllegalArgumentException() {
        FixedIncomeEngine.applyCompoundReturn(
            initialAmountCents = 100_000L,
            monthlyRate = 0.008,
            months = -1
        )
    }

    // -------------------------------------------------------------------------
    // calculateEarnings
    // -------------------------------------------------------------------------

    @Test
    fun calculateEarnings_retornaDiferencaEntreValorFinalEInicial() {
        val initial = 100_000L
        val rate = 0.008
        val months = 12

        val earnings = FixedIncomeEngine.calculateEarnings(initial, rate, months)
        val expectedFinal = FixedIncomeEngine.applyCompoundReturn(initial, rate, months)

        assertEquals(expectedFinal - initial, earnings)
    }

    @Test
    fun calculateEarnings_taxaZero_retornaZero() {
        val earnings = FixedIncomeEngine.calculateEarnings(
            initialAmountCents = 100_000L,
            monthlyRate = 0.0,
            months = 12
        )
        assertEquals(0L, earnings)
    }

    @Test
    fun calculateEarnings_zeroMeses_retornaZero() {
        val earnings = FixedIncomeEngine.calculateEarnings(
            initialAmountCents = 100_000L,
            monthlyRate = 0.008,
            months = 0
        )
        assertEquals(0L, earnings)
    }

    @Test
    fun calculateEarnings_umMes_retornaOitocentosCentavos() {
        // R$ 1.000,00 * 0,8% = R$ 8,00 = 800 centavos
        val earnings = FixedIncomeEngine.calculateEarnings(
            initialAmountCents = 100_000L,
            monthlyRate = 0.008,
            months = 1
        )
        assertEquals(800L, earnings)
    }
}
