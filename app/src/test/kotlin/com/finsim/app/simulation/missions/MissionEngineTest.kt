package com.finsim.app.simulation.missions

import com.finsim.app.domain.model.UserMissionProgress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MissionEngineTest {

    private val profileId = 1L

    private fun state(
        currentMonth: Int = 1,
        paidBills: Int = 0,
        totalBills: Int = 0,
        reserveCents: Long = 0,
        fixedIncomeCents: Long = 0,
        hasAnyInvestment: Boolean = false,
    ) = MissionEngine.SimulationState(
        profileId = profileId,
        currentMonth = currentMonth,
        paidBillsCount = paidBills,
        totalBillsCount = totalBills,
        reserveBalanceCents = reserveCents,
        fixedIncomeBalanceCents = fixedIncomeCents,
        hasAnyInvestment = hasAnyInvestment,
    )

    @Test
    fun `initializeMissions cria progresso zerado para todas as missoes do catalogo`() {
        val missions = MissionEngine.initializeMissions(profileId)
        assertEquals(MissionCatalog.all.size, missions.size)
        assertTrue(missions.all { !it.isCompleted })
        assertTrue(missions.all { it.currentProgress == 0L })
    }

    @Test
    fun `FIRST_BILL_PAID e completada quando pelo menos uma conta foi paga`() {
        val existing = MissionEngine.initializeMissions(profileId)
        val updates = MissionEngine.evaluate(existing, state(paidBills = 1, totalBills = 3))
        val mission = updates.find { it.missionId == MissionCatalog.FIRST_BILL_PAID }
        assertTrue(mission?.isCompleted == true)
    }

    @Test
    fun `FIRST_BILL_PAID nao e completada sem contas pagas`() {
        val existing = MissionEngine.initializeMissions(profileId)
        val updates = MissionEngine.evaluate(existing, state(paidBills = 0, totalBills = 3))
        val mission = updates.find { it.missionId == MissionCatalog.FIRST_BILL_PAID }
        assertFalse(mission?.isCompleted ?: false)
    }

    @Test
    fun `ALL_BILLS_PAID e completada apenas quando todas as contas estao pagas`() {
        val existing = MissionEngine.initializeMissions(profileId)
        val updates = MissionEngine.evaluate(existing, state(paidBills = 3, totalBills = 3))
        val mission = updates.find { it.missionId == MissionCatalog.ALL_BILLS_PAID }
        assertTrue(mission?.isCompleted == true)
    }

    @Test
    fun `ALL_BILLS_PAID nao e completada com contas pendentes`() {
        val existing = MissionEngine.initializeMissions(profileId)
        val updates = MissionEngine.evaluate(existing, state(paidBills = 2, totalBills = 3))
        val mission = updates.find { it.missionId == MissionCatalog.ALL_BILLS_PAID }
        assertFalse(mission?.isCompleted ?: false)
    }

    @Test
    fun `BUILD_RESERVE progresso reflete saldo atual da reserva`() {
        val existing = MissionEngine.initializeMissions(profileId)
        val updates = MissionEngine.evaluate(existing, state(reserveCents = 15_000L))
        val mission = updates.find { it.missionId == MissionCatalog.BUILD_RESERVE }
        assertEquals(15_000L, mission?.currentProgress)
        assertFalse(mission?.isCompleted ?: true)
    }

    @Test
    fun `BUILD_RESERVE e completada ao atingir meta de 30000 centavos`() {
        val existing = MissionEngine.initializeMissions(profileId)
        val updates = MissionEngine.evaluate(existing, state(reserveCents = 30_000L))
        val mission = updates.find { it.missionId == MissionCatalog.BUILD_RESERVE }
        assertTrue(mission?.isCompleted == true)
    }

    @Test
    fun `FIRST_INVESTMENT e completada quando hasAnyInvestment for true`() {
        val existing = MissionEngine.initializeMissions(profileId)
        val updates = MissionEngine.evaluate(existing, state(hasAnyInvestment = true))
        val mission = updates.find { it.missionId == MissionCatalog.FIRST_INVESTMENT }
        assertTrue(mission?.isCompleted == true)
    }

    @Test
    fun `ADVANCE_3_MONTHS registra mes atual como progresso`() {
        val existing = MissionEngine.initializeMissions(profileId)
        val updates = MissionEngine.evaluate(existing, state(currentMonth = 2))
        val mission = updates.find { it.missionId == MissionCatalog.ADVANCE_3_MONTHS }
        assertEquals(2L, mission?.currentProgress)
        assertFalse(mission?.isCompleted ?: true)
    }

    @Test
    fun `ADVANCE_3_MONTHS e completada no mes 3`() {
        val existing = MissionEngine.initializeMissions(profileId)
        val updates = MissionEngine.evaluate(existing, state(currentMonth = 3))
        val mission = updates.find { it.missionId == MissionCatalog.ADVANCE_3_MONTHS }
        assertTrue(mission?.isCompleted == true)
        assertEquals(3, mission?.completedMonth)
    }

    @Test
    fun `missoes ja completadas nao sao reavaliadas`() {
        val alreadyCompleted = MissionEngine.initializeMissions(profileId).map { progress ->
            if (progress.missionId == MissionCatalog.FIRST_INVESTMENT)
                progress.copy(isCompleted = true, completedMonth = 1, currentProgress = 1)
            else progress
        }
        val updates = MissionEngine.evaluate(alreadyCompleted, state(hasAnyInvestment = true))
        assertTrue(updates.none { it.missionId == MissionCatalog.FIRST_INVESTMENT })
    }

    @Test
    fun `newlyCompleted retorna apenas missoes concluidas no mes atual`() {
        val missions = listOf(
            UserMissionProgress(profileId = 1, missionId = "a", currentProgress = 1, isCompleted = true, completedMonth = 3),
            UserMissionProgress(profileId = 1, missionId = "b", currentProgress = 1, isCompleted = true, completedMonth = 2),
        )
        val result = MissionEngine.newlyCompleted(missions, currentMonth = 3)
        assertEquals(1, result.size)
        assertEquals("a", result[0].missionId)
    }
}
