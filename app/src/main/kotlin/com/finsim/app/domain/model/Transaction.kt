package com.finsim.app.domain.model

/**
 * Registro imutável de uma movimentação na conta virtual.
 * [amount] é sempre positivo em centavos; [type] define a direção.
 */
data class Transaction(
    val id: Long = 0,
    val accountId: Long,
    val type: TransactionType,
    val amount: Long,           // centavos, positivo
    val description: String,
    val month: Int,             // mês simulado
    val createdAt: Long = System.currentTimeMillis()  // epoch millis
)

enum class TransactionType {
    INCOME,
    BILL_PAYMENT,
    RESERVE_TRANSFER,
    INVESTMENT_APPLICATION,
    INVESTMENT_REDEMPTION,
    EVENT
}
