package com.finsim.app.presentation.common

/**
 * Formata um valor em centavos (Long) para exibição em reais.
 * Exemplo: 150000L → "R$ 1500,00"
 *
 * Esta função é usada exclusivamente na camada de apresentação.
 * Nenhuma regra financeira deve depender dela.
 */
fun Long.toCurrency(): String {
    val reais = this / 100
    val centavos = this % 100
    return "R$ %d,%02d".format(reais, centavos)
}
