package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.FixedIncomeProductType
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.FixedIncomeInvestmentRepository
import com.finsim.app.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class InvestInFixedIncomeUseCaseTest {

    private val accountRepository: AccountRepository = mockk(relaxed = true)
    private val investmentRepository: FixedIncomeInvestmentRepository = mockk(relaxed = true)
    private val transactionRepository: TransactionRepository = mockk(relaxed = true)

    private val useCase = InvestInFixedIncomeUseCase(
        accountRepository = accountRepository,
        investmentRepository = investmentRepository,
        transactionRepository = transactionRepository
    )

    private fun buildAccount(balance: Long) = Account(
        id = 1L,
        profileId = 1L,
        balance = balance,
        emergencyReserveBalance = 0L,
        updatedAt = 0L
    )

    // --- Cenário 1: saldo insuficiente ---

    @Test
    fun `quando o saldo é insuficiente deve retornar Failure`() = runTest {
        val account = buildAccount(balance = 10_000L)

        val result = useCase(account, amount = 50_000L, profileId = 1L, currentMonth = 1)

        assertIs<UseCaseResult.Failure>(result)
        assert(result.educationalMessage.isNotBlank())
    }

    @Test
    fun `quando o saldo é insuficiente nenhum repositório deve ser chamado`() = runTest {
        val account = buildAccount(balance = 10_000L)

        useCase(account, amount = 50_000L, profileId = 1L, currentMonth = 1)

        coVerify(exactly = 0) { investmentRepository.save(any()) }
        coVerify(exactly = 0) { accountRepository.update(any()) }
        coVerify(exactly = 0) { transactionRepository.save(any()) }
    }

    @Test
    fun `quando o amount é zero deve retornar Failure`() = runTest {
        val account = buildAccount(balance = 100_000L)

        val result = useCase(account, amount = 0L, profileId = 1L, currentMonth = 1)

        assertIs<UseCaseResult.Failure>(result)
    }

    // --- Cenário 2: investimento bem-sucedido ---

    @Test
    fun `investimento bem-sucedido deve retornar Success`() = runTest {
        coEvery { investmentRepository.save(any()) } returns 5L
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 200_000L)

        val result = useCase(account, amount = 100_000L, profileId = 1L, currentMonth = 2)

        assertIs<UseCaseResult.Success<*>>(result)
    }

    @Test
    fun `investimento bem-sucedido deve criar investimento com taxa de 80 bps`() = runTest {
        coEvery { investmentRepository.save(any()) } returns 5L
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 200_000L)

        val result = useCase(account, amount = 100_000L, profileId = 1L, currentMonth = 2)
            as UseCaseResult.Success

        assertEquals(80, result.data.monthlyRateBps)
    }

    @Test
    fun `investimento bem-sucedido deve usar produto TESOURO_SELIC_SIMULADO`() = runTest {
        coEvery { investmentRepository.save(any()) } returns 5L
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 200_000L)

        val result = useCase(account, amount = 100_000L, profileId = 1L, currentMonth = 2)
            as UseCaseResult.Success

        assertEquals(FixedIncomeProductType.TESOURO_SELIC_SIMULADO, result.data.productType)
    }

    @Test
    fun `investimento bem-sucedido deve reduzir o saldo da conta`() = runTest {
        coEvery { investmentRepository.save(any()) } returns 5L
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 200_000L)

        useCase(account, amount = 100_000L, profileId = 1L, currentMonth = 2)

        coVerify(exactly = 1) {
            accountRepository.update(match { it.balance == 100_000L })
        }
    }

    @Test
    fun `investimento bem-sucedido deve registrar transação de aplicação`() = runTest {
        coEvery { investmentRepository.save(any()) } returns 5L
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 200_000L)

        useCase(account, amount = 100_000L, profileId = 1L, currentMonth = 2)

        coVerify(exactly = 1) {
            transactionRepository.save(match { it.amount == 100_000L && it.month == 2 })
        }
    }

    @Test
    fun `investimento deve definir startMonth corretamente`() = runTest {
        coEvery { investmentRepository.save(any()) } returns 5L
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 200_000L)

        val result = useCase(account, amount = 50_000L, profileId = 1L, currentMonth = 4)
            as UseCaseResult.Success

        assertEquals(4, result.data.startMonth)
    }

    @Test
    fun `investimento deve ter maturityMonth nulo (Tesouro Selic sem vencimento)`() = runTest {
        coEvery { investmentRepository.save(any()) } returns 5L
        coEvery { transactionRepository.save(any()) } returns 1L

        val account = buildAccount(balance = 200_000L)

        val result = useCase(account, amount = 50_000L, profileId = 1L, currentMonth = 1)
            as UseCaseResult.Success

        assertEquals(null, result.data.maturityMonth)
    }
}
