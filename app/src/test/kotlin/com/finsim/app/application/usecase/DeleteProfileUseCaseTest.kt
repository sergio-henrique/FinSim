package com.finsim.app.application.usecase

import com.finsim.app.domain.repository.UserProfileRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteProfileUseCaseTest {

    private val repository: UserProfileRepository = mockk(relaxed = true)
    private val useCase = DeleteProfileUseCase(repository)

    @Test
    fun `deve chamar deleteById com o id correto`() = runTest {
        useCase(42L)
        coVerify(exactly = 1) { repository.deleteById(42L) }
    }

    @Test
    fun `ids diferentes devem ser passados corretamente`() = runTest {
        useCase(1L)
        useCase(999L)
        coVerify(exactly = 1) { repository.deleteById(1L) }
        coVerify(exactly = 1) { repository.deleteById(999L) }
    }
}
