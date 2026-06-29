package com.finsim.app.domain.model

/**
 * Investimento simulado de renda fixa.
 *
 * [monthlyRateBps] em pontos-base (1 bps = 0,01%).
 * [investedAmount] e [currentAmount] em centavos (Long).
 */
data class FixedIncomeInvestment(
    val id: Long = 0,
    val profileId: Long,
    val productType: FixedIncomeProductType,
    val investedAmount: Long,       // centavos
    val currentAmount: Long,        // centavos
    val monthlyRateBps: Int,        // pontos-base (ex: 80 = 0,80%/mês)
    val startMonth: Int,
    val maturityMonth: Int?,
    val liquidityType: LiquidityType,
    val createdAt: Long             // epoch millis
) {
    // Converte bps para decimal para uso no motor de simulação (80 bps → 0.008)
    val monthlyRate: Double get() = monthlyRateBps / 10_000.0
}

enum class FixedIncomeProductType {
    TESOURO_SELIC_SIMULADO,
    CDB_SIMULADO
}

enum class LiquidityType {
    DAILY,
    ON_MATURITY
}
