package com.finsim.app.domain.model

/**
 * Evento financeiro inesperado gerado pela simulação.
 *
 * Conceito pedagógico: imprevistos acontecem. Uma reserva de emergência
 * existe exatamente para absorver esses eventos sem comprometer o orçamento
 * regular ou forçar o resgate de investimentos.
 */
data class RandomEvent(
    val title: String,
    val description: String,
    val amountCents: Long,
    val educationalMessage: String,
    val category: RandomEventCategory,
)

enum class RandomEventCategory {
    HEALTH,
    HOME,
    TRANSPORT,
    APPLIANCE,
    PET,
}
