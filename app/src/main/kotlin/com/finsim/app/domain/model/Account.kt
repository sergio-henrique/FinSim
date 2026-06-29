package com.finsim.app.domain.model

/**
 * Conta corrente virtual do usuário.
 * Valores em centavos (Long).
 */
data class Account(
    val id: Long = 0,
    val profileId: Long,
    val balance: Long,                  // centavos
    val emergencyReserveBalance: Long,  // centavos
    val updatedAt: Long = System.currentTimeMillis()  // epoch millis
) {
    val totalBalance: Long get() = balance + emergencyReserveBalance
}
