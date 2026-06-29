package com.finsim.app.application.usecase

/**
 * Resultado padronizado para todos os casos de uso do FinSim.
 *
 * [Failure.educationalMessage] é sempre uma mensagem pensada para o público
 * jovem: sem tom punitivo, sem linguagem técnica e com orientação didática
 * sobre o que o usuário pode fazer em seguida.
 */
sealed class UseCaseResult<out T> {
    data class Success<T>(val data: T) : UseCaseResult<T>()
    data class Failure(val educationalMessage: String) : UseCaseResult<Nothing>()
}
