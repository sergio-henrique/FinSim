package com.finsim.app.simulation.fixedincome

/**
 * Resultado imutável de uma simulação de renda fixa para um mês.
 *
 * Esta classe carrega tanto os valores numéricos quanto a mensagem
 * pedagógica pré-formatada, permitindo que a UI exiba o resultado
 * sem precisar conhecer regras de negócio.
 *
 * Conceito pedagógico sobre imutabilidade:
 * O resultado de uma simulação passada nunca muda — assim como o extrato
 * bancário real. Usar data class imutável reflete essa semântica.
 *
 * @property previousAmountCents Valor do investimento antes do rendimento (centavos).
 * @property newAmountCents      Valor do investimento após o rendimento (centavos).
 * @property earningsCents       Quanto o investimento rendeu no período (centavos).
 * @property monthlyRate         Taxa aplicada no período (decimal, ex: 0.008).
 * @property productName         Nome do produto simulado (ex: "TESOURO_SELIC_SIMULADO").
 * @property educationalMessage  Mensagem explicativa para o usuário sobre o rendimento.
 */
data class FixedIncomeResult(
    val previousAmountCents: Long,
    val newAmountCents: Long,
    val earningsCents: Long,
    val monthlyRate: Double,
    val productName: String,
    val educationalMessage: String
) {
    /**
     * Percentual de rendimento do período, calculado sobre o valor anterior.
     *
     * Fórmula: percentual = (rendimento / valor_anterior) * 100
     *
     * Conceito pedagógico: este percentual é equivalente à taxa mensal
     * aplicada, mas expresso de forma que o usuário possa relacionar
     * o número ao valor ganho em reais. Útil para mostrar "você ganhou X%
     * este mês" de forma intuitiva.
     *
     * Retorna 0.0 se o valor anterior for zero (evita divisão por zero).
     */
    val earningsPercentage: Double
        get() = if (previousAmountCents > 0)
            (earningsCents.toDouble() / previousAmountCents) * 100.0
        else 0.0
}
