package com.finsim.app.simulation.challenges

import com.finsim.app.domain.model.Challenge
import com.finsim.app.domain.model.ChallengeCriteriaType
import com.finsim.app.domain.model.ChallengeProgress
import com.finsim.app.domain.model.ChallengeStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ChallengeEngineTest {

    private val reserveChallenge = Challenge(
        id = "test_reserve",
        title = "Reserva",
        description = "",
        educationalMessage = "",
        emoji = "🛡️",
        durationMonths = 6,
        criteriaType = ChallengeCriteriaType.MINIMUM_RESERVE,
        criteriaValueCents = 150_000L,
    )

    private val wealthChallenge = Challenge(
        id = "test_wealth",
        title = "Patrimônio",
        description = "",
        educationalMessage = "",
        emoji = "📈",
        durationMonths = 8,
        criteriaType = ChallengeCriteriaType.MINIMUM_WEALTH,
        criteriaValueCents = 500_000L,
    )

    private fun activeProgress(challengeId: String, startMonth: Int = 1) = ChallengeProgress(
        id = 1L,
        profileId = 1L,
        challengeId = challengeId,
        status = ChallengeStatus.ACTIVE,
        startMonth = startMonth,
    )

    // --- MINIMUM_RESERVE ---

    @Test
    fun `completa quando reserva atinge meta dentro do prazo`() {
        val state = ChallengeEngine.MonthEndState(
            currentMonth = 4, reserveBalanceCents = 150_000L, totalWealthCents = 200_000L,
        )
        val result = ChallengeEngine.evaluate(reserveChallenge, activeProgress("test_reserve"), state)
        assertEquals(ChallengeStatus.COMPLETED, result.status)
        assertEquals(4, result.resolvedMonth)
    }

    @Test
    fun `continua ativo quando reserva abaixo da meta e prazo nao expirou`() {
        val state = ChallengeEngine.MonthEndState(
            currentMonth = 3, reserveBalanceCents = 50_000L, totalWealthCents = 80_000L,
        )
        val result = ChallengeEngine.evaluate(reserveChallenge, activeProgress("test_reserve"), state)
        assertEquals(ChallengeStatus.ACTIVE, result.status)
    }

    @Test
    fun `falha quando prazo expira sem atingir reserva`() {
        val state = ChallengeEngine.MonthEndState(
            currentMonth = 7, reserveBalanceCents = 100_000L, totalWealthCents = 120_000L,
        )
        val result = ChallengeEngine.evaluate(reserveChallenge, activeProgress("test_reserve", startMonth = 1), state)
        assertEquals(ChallengeStatus.FAILED, result.status)
        assertEquals(7, result.resolvedMonth)
    }

    @Test
    fun `completa exatamente no ultimo mes do prazo`() {
        val state = ChallengeEngine.MonthEndState(
            currentMonth = 7, reserveBalanceCents = 200_000L, totalWealthCents = 250_000L,
        )
        val result = ChallengeEngine.evaluate(reserveChallenge, activeProgress("test_reserve", startMonth = 1), state)
        assertEquals(ChallengeStatus.COMPLETED, result.status)
    }

    // --- MINIMUM_WEALTH ---

    @Test
    fun `completa quando patrimonio atinge meta`() {
        val state = ChallengeEngine.MonthEndState(
            currentMonth = 5, reserveBalanceCents = 0L, totalWealthCents = 500_000L,
        )
        val result = ChallengeEngine.evaluate(wealthChallenge, activeProgress("test_wealth"), state)
        assertEquals(ChallengeStatus.COMPLETED, result.status)
    }

    @Test
    fun `falha quando patrimonio nao atinge meta no prazo`() {
        val state = ChallengeEngine.MonthEndState(
            currentMonth = 9, reserveBalanceCents = 0L, totalWealthCents = 400_000L,
        )
        val result = ChallengeEngine.evaluate(wealthChallenge, activeProgress("test_wealth", startMonth = 1), state)
        assertEquals(ChallengeStatus.FAILED, result.status)
    }

    // --- Estado já resolvido não é reavaliado ---

    @Test
    fun `desafio ja concluido nao e reavaliado`() {
        val completed = activeProgress("test_reserve").copy(status = ChallengeStatus.COMPLETED, resolvedMonth = 3)
        val state = ChallengeEngine.MonthEndState(
            currentMonth = 10, reserveBalanceCents = 500_000L, totalWealthCents = 1_000_000L,
        )
        val result = ChallengeEngine.evaluate(reserveChallenge, completed, state)
        assertEquals(ChallengeStatus.COMPLETED, result.status)
        assertEquals(3, result.resolvedMonth)
    }

    @Test
    fun `desafio ja fracassado nao e reavaliado`() {
        val failed = activeProgress("test_reserve").copy(status = ChallengeStatus.FAILED, resolvedMonth = 7)
        val state = ChallengeEngine.MonthEndState(
            currentMonth = 10, reserveBalanceCents = 500_000L, totalWealthCents = 1_000_000L,
        )
        val result = ChallengeEngine.evaluate(reserveChallenge, failed, state)
        assertEquals(ChallengeStatus.FAILED, result.status)
    }

    // --- progressPercent ---

    @Test
    fun `progressPercent para reserva calcula corretamente`() {
        val state = ChallengeEngine.MonthEndState(
            currentMonth = 1, reserveBalanceCents = 75_000L, totalWealthCents = 0L,
        )
        val pct = ChallengeEngine.progressPercent(reserveChallenge, state)
        assertEquals(0.5f, pct, 0.001f)
    }

    @Test
    fun `progressPercent e limitado a 1f quando acima da meta`() {
        val state = ChallengeEngine.MonthEndState(
            currentMonth = 1, reserveBalanceCents = 300_000L, totalWealthCents = 0L,
        )
        val pct = ChallengeEngine.progressPercent(reserveChallenge, state)
        assertEquals(1f, pct, 0.001f)
    }
}
