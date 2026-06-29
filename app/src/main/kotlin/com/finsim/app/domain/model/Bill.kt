package com.finsim.app.domain.model

/**
 * Despesa mensal virtual. [amount] em centavos.
 */
data class Bill(
    val id: Long = 0,
    val profileId: Long,
    val name: String,
    val amount: Long,       // centavos
    val month: Int,
    val isPaid: Boolean,
    val category: BillCategory,
    val dueMonth: Int
)

enum class BillCategory {
    HOUSING, FOOD, TRANSPORT, EDUCATION, HEALTH, LEISURE, OTHER
}
