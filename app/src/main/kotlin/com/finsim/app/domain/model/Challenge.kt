package com.finsim.app.domain.model

/**
 * Desafio educativo pré-definido.
 * Os desafios são estáticos (catálogo); apenas o progresso é persistido.
 */
data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val educationalMessage: String,
    val emoji: String,
    val durationMonths: Int,
    val criteriaType: ChallengeCriteriaType,
    val criteriaValueCents: Long,
)

enum class ChallengeCriteriaType {
    MINIMUM_RESERVE,  // reserva de emergência >= valor alvo
    MINIMUM_WEALTH,   // patrimônio total >= valor alvo
}

enum class ChallengeStatus { NOT_STARTED, ACTIVE, COMPLETED, FAILED }

/**
 * Progresso de um perfil em um desafio específico.
 * [startMonth] é o mês simulado em que o desafio foi iniciado.
 * [resolvedMonth] é preenchido quando o desafio termina (concluído ou falhou).
 */
data class ChallengeProgress(
    val id: Long = 0,
    val profileId: Long,
    val challengeId: String,
    val status: ChallengeStatus,
    val startMonth: Int,
    val resolvedMonth: Int? = null,
)
