package com.finsim.app.simulation.missions

import com.finsim.app.domain.model.UserMissionProgress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AchievementEngineTest {

    private val profileId = 1L

    private fun context(
        currentMonth: Int = 1,
        totalWealthCents: Long = 0,
        reserveCents: Long = 0,
        hasInvestment: Boolean = false,
        allBillsPaid: Boolean = false,
        survivedWithReserve: Boolean = false,
        newlyCompleted: List<UserMissionProgress> = emptyList(),
    ) = AchievementEngine.AchievementContext(
        profileId = profileId,
        currentMonth = currentMonth,
        totalWealthCents = totalWealthCents,
        reserveBalanceCents = reserveCents,
        hasAnyInvestment = hasInvestment,
        allBillsPaidThisMonth = allBillsPaid,
        survivedEventWithReserveIntact = survivedWithReserve,
        newlyCompletedMissions = newlyCompleted,
    )

    private fun completedMission(id: String) = UserMissionProgress(
        profileId = profileId,
        missionId = id,
        currentProgress = 1,
        isCompleted = true,
        completedMonth = 1,
    )

    @Test
    fun `FIRST_STEPS e desbloqueada quando alguma missao e concluida`() {
        val ctx = context(newlyCompleted = listOf(completedMission(MissionCatalog.FIRST_BILL_PAID)))
        val result = AchievementEngine.evaluate(ctx, emptySet())
        assertTrue(result.any { it.id == AchievementCatalog.FIRST_STEPS })
    }

    @Test
    fun `ORGANIZED e desbloqueada quando todas as contas estao pagas`() {
        val ctx = context(allBillsPaid = true)
        val result = AchievementEngine.evaluate(ctx, emptySet())
        assertTrue(result.any { it.id == AchievementCatalog.ORGANIZED })
    }

    @Test
    fun `SAVER e desbloqueada com reserva de 30000 centavos ou mais`() {
        val ctx = context(reserveCents = 30_000L)
        val result = AchievementEngine.evaluate(ctx, emptySet())
        assertTrue(result.any { it.id == AchievementCatalog.SAVER })
    }

    @Test
    fun `SAVER nao e desbloqueada com reserva insuficiente`() {
        val ctx = context(reserveCents = 29_999L)
        val result = AchievementEngine.evaluate(ctx, emptySet())
        assertFalse(result.any { it.id == AchievementCatalog.SAVER })
    }

    @Test
    fun `INVESTOR e desbloqueada quando usuario tem investimento`() {
        val ctx = context(hasInvestment = true)
        val result = AchievementEngine.evaluate(ctx, emptySet())
        assertTrue(result.any { it.id == AchievementCatalog.INVESTOR })
    }

    @Test
    fun `RESILIENT e desbloqueada ao sobreviver imprevisto com reserva intacta`() {
        val ctx = context(survivedWithReserve = true)
        val result = AchievementEngine.evaluate(ctx, emptySet())
        assertTrue(result.any { it.id == AchievementCatalog.RESILIENT })
    }

    @Test
    fun `BUILDER e desbloqueado com patrimonio de 500000 centavos`() {
        val ctx = context(totalWealthCents = 500_000L)
        val result = AchievementEngine.evaluate(ctx, emptySet())
        assertTrue(result.any { it.id == AchievementCatalog.BUILDER })
    }

    @Test
    fun `MONTH_MASTER e desbloqueado no mes 3 ou alem`() {
        val ctx = context(currentMonth = 3)
        val result = AchievementEngine.evaluate(ctx, emptySet())
        assertTrue(result.any { it.id == AchievementCatalog.MONTH_MASTER })
    }

    @Test
    fun `conquistas ja desbloqueadas nao sao retornadas novamente`() {
        val ctx = context(hasInvestment = true, allBillsPaid = true)
        val alreadyUnlocked = setOf(AchievementCatalog.INVESTOR, AchievementCatalog.ORGANIZED)
        val result = AchievementEngine.evaluate(ctx, alreadyUnlocked)
        assertFalse(result.any { it.id == AchievementCatalog.INVESTOR })
        assertFalse(result.any { it.id == AchievementCatalog.ORGANIZED })
    }

    @Test
    fun `nenhuma conquista desbloqueada para usuario sem historico`() {
        val ctx = context()
        val result = AchievementEngine.evaluate(ctx, emptySet())
        assertEquals(0, result.size)
    }
}
