package com.finsim.app.domain.model

/**
 * Foto do estado patrimonial ao fim de cada mês simulado.
 * Todos os valores monetários em centavos (Long).
 * [financialHealthScore] de 0 a 100.
 */
data class MonthlySnapshot(
    val id: Long = 0,
    val profileId: Long,
    val month: Int,
    val accountBalance: Long,
    val reserveBalance: Long,
    val fixedIncomeBalance: Long,
    val totalWealth: Long,
    val billsPaidAmount: Long,
    val billsPendingAmount: Long,
    val financialHealthScore: Int
)
