package com.finsim.app.simulation.challenges

import com.finsim.app.domain.model.Challenge
import com.finsim.app.domain.model.ChallengeCriteriaType
import com.finsim.app.domain.model.ChallengeProgress
import com.finsim.app.domain.model.ChallengeStatus

/**
 * Motor puro de avaliação de desafios.
 *
 * Critérios de resolução (avaliados ao fim de cada mês):
 *   - COMPLETED: critério atingido dentro do prazo.
 *   - FAILED: prazo expirou sem atingir o critério.
 *   - ACTIVE: prazo não expirou e critério não atingido ainda.
 */
object ChallengeEngine {

    data class MonthEndState(
        val currentMonth: Int,
        val reserveBalanceCents: Long,
        val totalWealthCents: Long,
    )

    fun evaluate(
        challenge: Challenge,
        progress: ChallengeProgress,
        state: MonthEndState,
    ): ChallengeProgress {
        if (progress.status != ChallengeStatus.ACTIVE) return progress

        val monthsElapsed = state.currentMonth - progress.startMonth
        val criteriaReached = when (challenge.criteriaType) {
            ChallengeCriteriaType.MINIMUM_RESERVE -> state.reserveBalanceCents >= challenge.criteriaValueCents
            ChallengeCriteriaType.MINIMUM_WEALTH -> state.totalWealthCents >= challenge.criteriaValueCents
        }

        return when {
            criteriaReached -> progress.copy(
                status = ChallengeStatus.COMPLETED,
                resolvedMonth = state.currentMonth,
            )
            monthsElapsed >= challenge.durationMonths -> progress.copy(
                status = ChallengeStatus.FAILED,
                resolvedMonth = state.currentMonth,
            )
            else -> progress
        }
    }

    fun progressPercent(challenge: Challenge, state: MonthEndState): Float {
        val current = when (challenge.criteriaType) {
            ChallengeCriteriaType.MINIMUM_RESERVE -> state.reserveBalanceCents
            ChallengeCriteriaType.MINIMUM_WEALTH -> state.totalWealthCents
        }
        return (current.toFloat() / challenge.criteriaValueCents).coerceIn(0f, 1f)
    }
}
