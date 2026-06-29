package com.finsim.app.simulation.economy

import java.math.RoundingMode

/**
 * Motor de inflação simulada.
 *
 * Conceito pedagógico:
 * A inflação é o aumento generalizado dos preços ao longo do tempo.
 * Ela impacta as contas mensais, o poder de compra e o retorno real
 * dos investimentos. Manter dinheiro parado na conta corrente significa
 * perder poder de compra a cada mês — daí a importância de investir.
 *
 * Retorno real = taxa nominal - inflação
 * Exemplo: CDB a 0,9%/mês com inflação de 0,4%/mês → retorno real ≈ 0,5%/mês.
 *
 * Este motor NÃO acessa banco de dados e NÃO tem efeitos colaterais.
 */
object InflationEngine {

    /**
     * Aplica inflação mensal a um valor em centavos.
     *
     * O resultado é arredondado para cima para que nenhuma conta
     * seja subestimada — pedagogicamente mais realista.
     *
     * @param amountCents        Valor original em centavos.
     * @param monthlyInflation   Taxa mensal em decimal (ex: 0.004 = 0,4%).
     * @return Valor ajustado pela inflação, em centavos.
     */
    fun applyMonthly(amountCents: Long, monthlyInflation: Double): Long {
        val inflated = amountCents * (1.0 + monthlyInflation)
        return inflated.toBigDecimal().setScale(0, RoundingMode.HALF_UP).toLong()
    }

    /**
     * Calcula a perda de poder de compra acumulada ao longo de [months] meses.
     *
     * Usado para exibir mensagem educativa sobre o impacto da inflação no tempo.
     *
     * @param originalCents    Valor original em centavos.
     * @param monthlyInflation Taxa mensal em decimal.
     * @param months           Número de meses passados.
     * @return Perda de poder de compra em centavos (sempre positivo).
     */
    fun accumulatedLoss(originalCents: Long, monthlyInflation: Double, months: Int): Long {
        if (months <= 0) return 0L
        val compounded = originalCents * Math.pow(1.0 + monthlyInflation, months.toDouble())
        return (compounded - originalCents).toLong().coerceAtLeast(0L)
    }

    /**
     * Retorna o retorno real de um investimento descontada a inflação.
     *
     * Fórmula de Fisher: (1 + nominal) / (1 + inflação) - 1
     *
     * @param nominalRate      Taxa nominal mensal (ex: 0.009 = 0,9%).
     * @param inflationRate    Taxa de inflação mensal (ex: 0.004 = 0,4%).
     * @return Taxa de retorno real mensal em decimal.
     */
    fun realReturn(nominalRate: Double, inflationRate: Double): Double =
        (1.0 + nominalRate) / (1.0 + inflationRate) - 1.0
}
