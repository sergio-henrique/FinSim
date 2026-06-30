package com.finsim.app.application.usecase

import com.finsim.app.domain.model.AgeRange
import com.finsim.app.domain.model.MonthlySnapshot
import com.finsim.app.domain.model.UserProfile
import com.finsim.app.domain.repository.MonthlySnapshotRepository
import com.finsim.app.domain.repository.UserProfileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRankingUseCaseTest {

    private val userProfileRepository: UserProfileRepository = mockk()
    private val snapshotRepository: MonthlySnapshotRepository = mockk()

    private val useCase = GetRankingUseCase(userProfileRepository, snapshotRepository)

    private fun profile(id: Long, name: String, month: Int = 1) = UserProfile(
        id = id, name = name, ageRange = AgeRange.TEEN,
        monthlyIncome = 100_000L, currentMonth = month, createdAt = 0L,
    )

    private fun snapshot(profileId: Long, score: Int, wealth: Long) = MonthlySnapshot(
        profileId = profileId, month = 1,
        accountBalance = wealth, reserveBalance = 0L,
        fixedIncomeBalance = 0L, totalWealth = wealth,
        billsPaidAmount = 0L, billsPendingAmount = 0L,
        financialHealthScore = score,
    )

    @Test
    fun `ranking com dois perfis ordena por score desc`() = runTest {
        val p1 = profile(1L, "Alice", month = 3)
        val p2 = profile(2L, "Bob", month = 5)
        coEvery { userProfileRepository.getAll() } returns flowOf(listOf(p1, p2))
        coEvery { snapshotRepository.getLatestByProfileId(1L) } returns snapshot(1L, score = 55, wealth = 50_000L)
        coEvery { snapshotRepository.getLatestByProfileId(2L) } returns snapshot(2L, score = 80, wealth = 30_000L)

        val ranking = useCase.invoke().first()

        assertEquals(2, ranking.size)
        assertEquals("Bob", ranking[0].profile.name)
        assertEquals(1, ranking[0].position)
        assertEquals("Alice", ranking[1].profile.name)
        assertEquals(2, ranking[1].position)
    }

    @Test
    fun `ranking desempate por patrimonio quando scores iguais`() = runTest {
        val p1 = profile(1L, "Alice")
        val p2 = profile(2L, "Bob")
        coEvery { userProfileRepository.getAll() } returns flowOf(listOf(p1, p2))
        coEvery { snapshotRepository.getLatestByProfileId(1L) } returns snapshot(1L, score = 70, wealth = 200_000L)
        coEvery { snapshotRepository.getLatestByProfileId(2L) } returns snapshot(2L, score = 70, wealth = 100_000L)

        val ranking = useCase.invoke().first()

        assertEquals("Alice", ranking[0].profile.name)
    }

    @Test
    fun `perfil sem snapshot recebe score zero`() = runTest {
        val p = profile(1L, "Carlos")
        coEvery { userProfileRepository.getAll() } returns flowOf(listOf(p))
        coEvery { snapshotRepository.getLatestByProfileId(1L) } returns null

        val ranking = useCase.invoke().first()

        assertEquals(0, ranking[0].latestHealthScore)
        assertEquals(0L, ranking[0].totalWealth)
    }

    @Test
    fun `ranking vazio quando nao ha perfis`() = runTest {
        coEvery { userProfileRepository.getAll() } returns flowOf(emptyList())

        val ranking = useCase.invoke().first()

        assertTrue(ranking.isEmpty())
    }

    @Test
    fun `meses simulados eh currentMonth menos 1`() = runTest {
        val p = profile(1L, "Dana", month = 7)
        coEvery { userProfileRepository.getAll() } returns flowOf(listOf(p))
        coEvery { snapshotRepository.getLatestByProfileId(1L) } returns null

        val ranking = useCase.invoke().first()

        assertEquals(6, ranking[0].monthsSimulated)
    }
}
