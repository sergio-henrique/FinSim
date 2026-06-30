package com.finsim.app.application.usecase

import com.finsim.app.domain.model.ChallengeProgress
import com.finsim.app.domain.model.ChallengeStatus
import com.finsim.app.domain.repository.ChallengeProgressRepository
import com.finsim.app.simulation.challenges.ChallengeCatalog
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetChallengesUseCaseTest {

    private val repository: ChallengeProgressRepository = mockk()
    private val useCase = GetChallengesUseCase(repository)

    @Test
    fun `retorna todos os desafios do catalogo com status NOT_STARTED quando sem progresso`() = runTest {
        every { repository.getByProfileId(1L) } returns flowOf(emptyList())

        val result = useCase.invoke(1L).first()

        assertEquals(ChallengeCatalog.all.size, result.size)
        result.forEach { assertEquals(ChallengeStatus.NOT_STARTED, it.status) }
    }

    @Test
    fun `desafio ativo e refletido corretamente`() = runTest {
        val progress = ChallengeProgress(
            id = 1L, profileId = 1L,
            challengeId = "colchao_seguranca",
            status = ChallengeStatus.ACTIVE,
            startMonth = 1,
        )
        every { repository.getByProfileId(1L) } returns flowOf(listOf(progress))

        val result = useCase.invoke(1L).first()

        val colchao = result.find { it.challenge.id == "colchao_seguranca" }!!
        assertEquals(ChallengeStatus.ACTIVE, colchao.status)
        assertEquals(progress, colchao.progress)
    }

    @Test
    fun `desafio concluido e refletido corretamente`() = runTest {
        val progress = ChallengeProgress(
            id = 2L, profileId = 1L,
            challengeId = "patrimonio_crescimento",
            status = ChallengeStatus.COMPLETED,
            startMonth = 1,
            resolvedMonth = 6,
        )
        every { repository.getByProfileId(1L) } returns flowOf(listOf(progress))

        val result = useCase.invoke(1L).first()

        val patrimonio = result.find { it.challenge.id == "patrimonio_crescimento" }!!
        assertEquals(ChallengeStatus.COMPLETED, patrimonio.status)
    }

    @Test
    fun `desafio sem progresso registrado tem progress nulo`() = runTest {
        every { repository.getByProfileId(1L) } returns flowOf(emptyList())

        val result = useCase.invoke(1L).first()

        result.forEach { assertEquals(null, it.progress) }
    }
}
