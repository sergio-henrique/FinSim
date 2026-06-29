package com.finsim.app.simulation.fixedincome

import kotlin.math.pow

/**
 * Motor de simulação de renda fixa.
 *
 * Conceito pedagógico — juros compostos:
 * A cada mês, o rendimento incide sobre o valor já acrescido dos rendimentos
 * anteriores. Isso faz o dinheiro "trabalhar para você" com o tempo, gerando
 * um efeito de crescimento acelerado no longo prazo.
 *
 * Todos os valores monetários são manipulados em centavos (Long) para evitar
 * erros de arredondamento de ponto flutuante em operações sucessivas.
 * O Double é usado apenas para o cálculo intermediário e truncado ao final.
 *
 * Este motor não acessa banco de dados — recebe valores, processa e retorna.
 */
object FixedIncomeEngine {

    /**
     * Aplica um rendimento mensal simples sobre um valor em centavos.
     *
     * Fórmula: valor_final = valor_atual * (1 + taxa_mensal)
     *
     * Exemplo pedagógico:
     *   R$ 1.000,00 investido a 0,8% ao mês
     *   → 100_000 * (1 + 0.008) = 100_800 centavos = R$ 1.008,00
     *
     * O resultado é truncado (toLong) — centavos fracionários são descartados,
     * o que é pedagogicamente honesto e evita acúmulo de erro.
     *
     * @param currentAmountCents Valor atual do investimento em centavos. Deve ser >= 0.
     * @param monthlyRate        Taxa mensal em decimal. Ex: 0.008 = 0,8%. Deve ser >= 0.
     * @return Novo saldo em centavos após o rendimento do mês.
     * @throws IllegalArgumentException se qualquer parâmetro for negativo.
     */
    fun applyMonthlyReturn(currentAmountCents: Long, monthlyRate: Double): Long {
        require(currentAmountCents >= 0) { "Valor não pode ser negativo: $currentAmountCents" }
        require(monthlyRate >= 0.0) { "Taxa não pode ser negativa: $monthlyRate" }

        // Fórmula: valor_final = valor_atual * (1 + taxa_mensal)
        val result = currentAmountCents * (1.0 + monthlyRate)
        return result.toLong()
    }

    /**
     * Calcula o valor acumulado após N meses de juros compostos.
     *
     * Fórmula: valor_final = valor_inicial * (1 + taxa_mensal) ^ n
     *
     * Conceito pedagógico — poder dos juros compostos no tempo:
     * Com 0,8% ao mês durante 12 meses, o rendimento total não é 9,6%
     * (0,8% × 12), mas sim ~10,03%, porque os juros de cada mês rendem
     * também no mês seguinte. Esse efeito fica cada vez maior com o tempo.
     *
     * Exemplo:
     *   R$ 1.000,00 a 0,8%/mês por 12 meses
     *   → 100_000 * (1.008)^12 ≈ 110_034 centavos = R$ 1.100,34
     *
     * @param initialAmountCents Valor inicial do investimento em centavos. Deve ser >= 0.
     * @param monthlyRate        Taxa mensal em decimal. Deve ser >= 0.
     * @param months             Número de meses do período. Deve ser >= 0.
     * @return Valor final em centavos após todos os meses de rendimento.
     * @throws IllegalArgumentException se qualquer parâmetro for negativo.
     */
    fun applyCompoundReturn(
        initialAmountCents: Long,
        monthlyRate: Double,
        months: Int
    ): Long {
        require(initialAmountCents >= 0) { "Valor não pode ser negativo: $initialAmountCents" }
        require(monthlyRate >= 0.0) { "Taxa não pode ser negativa: $monthlyRate" }
        require(months >= 0) { "Número de meses não pode ser negativo: $months" }

        // Fórmula: valor_final = valor_inicial * (1 + taxa_mensal)^n
        val result = initialAmountCents * (1.0 + monthlyRate).pow(months.toDouble())
        return result.toLong()
    }

    /**
     * Calcula o rendimento bruto (ganho) gerado em um período.
     *
     * Fórmula: rendimento = valor_final - valor_inicial
     *
     * Conceito pedagógico — visualizar o que o dinheiro gerou:
     * Separar o "principal" (valor que você colocou) do "rendimento"
     * (valor que o investimento produziu) ajuda o usuário a entender
     * que o dinheiro investido está "trabalhando" mesmo sem esforço.
     *
     * Exemplo:
     *   R$ 1.000,00 a 0,8%/mês por 12 meses
     *   → rendimento = R$ 1.100,34 - R$ 1.000,00 = R$ 100,34
     *
     * @param initialAmountCents Valor inicial em centavos. Deve ser >= 0.
     * @param monthlyRate        Taxa mensal em decimal. Deve ser >= 0.
     * @param months             Número de meses. Deve ser >= 0.
     * @return Rendimento líquido em centavos (sempre >= 0 quando a taxa >= 0).
     */
    fun calculateEarnings(
        initialAmountCents: Long,
        monthlyRate: Double,
        months: Int
    ): Long {
        val finalAmount = applyCompoundReturn(initialAmountCents, monthlyRate, months)
        return finalAmount - initialAmountCents
    }
}
