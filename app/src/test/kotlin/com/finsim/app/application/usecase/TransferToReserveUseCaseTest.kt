package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TransferToReserveUseCaseTest {

    private val accountRepository: AccountRepository = mockk(relaxed = true)
    private val transactionRepository: TransactionRepository = mockk(relaxed = true)

    private val useCase = TransferToReserveUseCase(
        accountRepository = accountRepository,
        transactionRepository = transactionRepository
    )

    private fun buildAccount(balance: Long, reserve: Long = 0L) = Account(
        id = 1L,
        profileId = 1L,
        balance = balance,
        emergencyReserveBalance = reserve,
        updatedAt = 0L
    )

    // --- Cenário 1: valor zero ---

    @Test
    fun `quando o amount é zero deve retornar Failure`() = runTest {
        val account = buildAccount(balance = 100_000L)

        val result = useCase(account, amount = 0L, currentMonth = 1)

        assertIs<UseCaseResult.Failure>(result)
        assert(result.educationalMessage.isNotBlank())
    }

    @Test
    fun `quando o amount é zero nenhum repositório deve ser chamado`() = runTest {
        val account = buildAccount(balance = 100_000L)

        useCase(account, amount = 0L, currentMonth = 1)

        coVerify(exactly = 0) { accountRepository.update(any()) }
        coVerify(exactly = 0) { transactionRepository.save(any()) }
    }

    // --- Cenário 2: amount maior que saldo ---

    @Test
    fun `quando o amount é maior que o saldo deve retornar Failure`() = runTest {
        val account = buildAccount(balance = 50_000L)

        val result = useCase(account, amount = 100_000L, currentMonth = 1)

        assertIs<UseCaseResult.Failure>(result)
        assert(result.educationalMessage.isNotBlank())
    }

    @Test
    fun `quando o amount é maior que o saldo nenhum repositório deve ser chamado`() = runTest {
        val account = buildAccount(balance = 50_000L)

        useCase(account, amount = 100_000L, currentMonth = 1)

        coVerify(exactly = 0) { accountRepository.update(any()) }
        coVerify(exactly = 0) { transactionRepository.save(any()) }
    }

    // --- Cenário 3: transferência bem-sucedida ---

    @Test
    fun `transferência bem-sucedida deve retornar Success`() = runTest {
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 100_000L, reserve = 20_000L)

        val result = useCase(account, amount = 30_000L, currentMonth = 2)

        assertIs<UseCaseResult.Success<Account>>(result)
    }

    @Test
    fun `transferência bem-sucedida deve reduzir o saldo livre`() = runTest {
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 100_000L)

        val result = useCase(account, amount = 30_000L, currentMonth = 1) as UseCaseResult.Success

        assertEquals(70_000L, result.data.balance)
    }

    @Test
    fun `transferência bem-sucedida deve aumentar a reserva de emergência`() = runTest {
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 100_000L, reserve = 20_000L)

        val result = useCase(account, amount = 30_000L, currentMonth = 1) as UseCaseResult.Success

        assertEquals(50_000L, result.data.emergencyReserveBalance)
    }

    @Test
    fun `transferência bem-sucedida deve persistir conta atualizada`() = runTest {
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 100_000L, reserve = 0L)

        useCase(account, amount = 40_000L, currentMonth = 1)

        coVerify(exactly = 1) {
            accountRepository.update(match { it.balance == 60_000L && it.emergencyReserveBalance == 40_000L })
        }
    }

    @Test
    fun `transferência bem-sucedida deve registrar transação`() = runTest {
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 100_000L)

        useCase(account, amount = 30_000L, currentMonth = 3)

        coVerify(exactly = 1) {
            transactionRepository.save(match { it.amount == 30_000L && it.month == 3 })
        }
    }

    @Test
    fun `transferência com amount negativo deve retornar Failure`() = runTest {
        val account = buildAccount(balance = 100_000L)

        val result = useCase(account, amount = -1L, currentMonth = 1)

        assertIs<UseCaseResult.Failure>(result)
    }
}
