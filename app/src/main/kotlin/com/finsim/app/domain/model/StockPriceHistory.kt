package com.finsim.app.domain.model

/**
 * Registro histórico do preço de um ativo em um mês específico.
 * Persistido a cada passagem de mês para alimentar gráficos.
 */
data class StockPriceHistory(
    val ticker: String,
    val month: Int,
    val priceCents: Long,
)
