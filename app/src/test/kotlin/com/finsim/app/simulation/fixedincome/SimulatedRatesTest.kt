package com.finsim.app.simulation.fixedincome

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testes unitarios do SimulatedRates.
 *
 * Verifica que as constantes pedagogicas estao corretas e que a funcao
 * de conversao mensal/anual reflete o comportamento dos juros compostos.
 */
class SimulatedRatesTest {

    @Test
    fun tesourSelicMonthly_valorEsperado_008() {
        // Taxa do Tesouro Selic simulado deve ser exatamente 0,8% ao mes
        assertEquals(0.008, SimulatedRates.TESOURO_SELIC_MONTHLY, 0.0)
    }

    @Test
    fun cdbMonthly_valorEsperado_009() {
        // Taxa do CDB simulado deve ser exatamente 0,9% ao mes
        assertEquals(0.009, SimulatedRates.CDB_MONTHLY, 0.0)
    }

    @Test
    fun cdbMonthly_maiorQueTesourSelic() {
        // O CDB deve ter taxa superior ao Tesouro Selic simulado,
        // ilustrando a relacao risco x retorno: maior risco, maior potencial
        assertTrue(
            "CDB deve ter taxa maior que Tesouro Selic",
            SimulatedRates.CDB_MONTHLY > SimulatedRates.TESOURO_SELIC_MONTHLY
        )
    }

    @Test
    fun monthlyToAnnual_taxaZero_retornaZero() {
        // Sem taxa mensal, nao ha taxa anual
        val annual = SimulatedRates.monthlyToAnnual(0.0)
        assertEquals(0.0, annual, 0.0001)
    }

    @Test
    fun monthlyToAnnual_tesouroSelic_retornaAproximadamente10Porcento() {
        // 0,8%/mes compostos por 12 meses resulta em ~10,03% ao ano
        // Prova que juros compostos geram mais que 0,8% * 12 = 9,6%
        val annual = SimulatedRates.monthlyToAnnual(SimulatedRates.TESOURO_SELIC_MONTHLY)
        assertTrue(
            "Taxa anual do Tesouro Selic simulado deve estar entre 9% e 11%, obtido $annual",
            annual in 0.09..0.11
        )
    }

    @Test
    fun monthlyToAnnual_taxaPositiva_maiorQue12VezesATaxaMensal() {
        // Comprova o efeito dos juros compostos:
        // taxa_anual_composta > taxa_mensal * 12 (taxa simples)
        val monthlyRate = 0.008
        val annualCompound = SimulatedRates.monthlyToAnnual(monthlyRate)
        val annualSimple = monthlyRate * 12

        assertTrue(
            "Taxa anual composta ($annualCompound) deve ser maior que simples ($annualSimple)",
            annualCompound > annualSimple
        )
    }

    @Test
    fun monthlyToAnnual_taxaConhecida_retornaValorCorreto() {
        // Verificacao precisa: (1 + 0.008)^12 - 1 = 0.10034...
        val annual = SimulatedRates.monthlyToAnnual(0.008)
        // Tolerancia de 0.0001 (0,01%) para variacao de ponto flutuante
        assertEquals(0.10034, annual, 0.0001)
    }
}
