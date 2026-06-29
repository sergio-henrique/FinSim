package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.Bill
import com.finsim.app.domain.model.BillCategory
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.BillRepository
import com.finsim.app.domain.repository.TransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PayBillUseCaseTest {

    private val accountRepository: AccountRepository = mockk(relaxed = true)
    private val billRepository: BillRepository = mockk(relaxed = true)
    private val transactionRepository: TransactionRepository = mockk(relaxed = true)

    private val useCase = PayBillUseCase(
        accountRepository = accountRepository,
        billRepository = billRepository,
        transactionRepository = transactionRepository
    )

    private fun buildAccount(balance: Long) = Account(
        id = 1L,
        profileId = 1L,
        balance = balance,
        emergencyReserveBalance = 0L,
        updatedAt = 0L
    )

    private fun buildBill(amount: Long, isPaid: Boolean = false) = Bill(
        id = 10L,
        profileId = 1L,
        name = "Aluguel",
        amount = amount,
        month = 1,
        isPaid = isPaid,
        category = BillCategory.HOUSING,
        dueMonth = 1
    )

    // --- Cenário 1: conta já paga ---

    @Test
    fun `quando a conta já foi paga deve retornar Failure com mensagem educativa`() = runTest {
        val bill = buildBill(amount = 50_000L, isPaid = true)
        val account = buildAccount(balance = 100_000L)

        val result = useCase(account, bill)

        assertIs<UseCaseResult.Failure>(result)
        assert(result.educationalMessage.isNotBlank())
    }

    @Test
    fun `quando a conta já foi paga nenhum repositório deve ser chamado`() = runTest {
        val bill = buildBill(amount = 50_000L, isPaid = true)
        val account = buildAccount(balance = 100_000L)

        useCase(account, bill)

        coVerify(exactly = 0) { accountRepository.update(any()) }
        coVerify(exactly = 0) { transactionRepository.save(any()) }
    }

    // --- Cenário 2: saldo insuficiente ---

    @Test
    fun `quando o saldo é insuficiente deve retornar Failure com mensagem educativa`() = runTest {
        val bill = buildBill(amount = 150_000L, isPaid = false)
        val account = buildAccount(balance = 100_000L)

        val result = useCase(account, bill)

        assertIs<UseCaseResult.Failure>(result)
        assert(result.educationalMessage.isNotBlank())
    }

    @Test
    fun `quando o saldo é insuficiente nenhum repositório deve ser chamado`() = runTest {
        val bill = buildBill(amount = 150_000L, isPaid = false)
        val account = buildAccount(balance = 100_000L)

        useCase(account, bill)

        coVerify(exactly = 0) { accountRepository.update(any()) }
        coVerify(exactly = 0) { transactionRepository.save(any()) }
    }

    // --- Cenário 3: pagamento bem-sucedido ---

    @Test
    fun `pagamento bem-sucedido deve retornar Success com saldo reduzido`() = runTest {
        coEvery { transactionRepository.save(any()) } returns 1L

        val bill = buildBill(amount = 50_000L, isPaid = false)
        val account = buildAccount(balance = 100_000L)

        val result = useCase(account, bill)

        assertIs<UseCaseResult.Success<Account>>(result)
        assertEquals(50_000L, result.data.balance)
    }

    @Test
    fun `pagamento bem-sucedido deve persistir conta atualizada`() = runTest {
        coEvery { transactionRepository.save(any()) } returns 1L

        val bill = buildBill(amount = 50_000L, isPaid = false)
        val account = buildAccount(balance = 100_000L)

        useCase(account, bill)

        coVerify(exactly = 1) {
            accountRepository.update(match { it.balance == 50_000L })
        }
    }

    @Test
    fun `pagamento bem-sucedido deve marcar a bill como paga`() = runTest {
        coEvery { transactionRepository.save(any()) } returns 1L

        val bill = buildBill(amount = 50_000L, isPaid = false)
        val account = buildAccount(balance = 100_000L)

        useCase(account, bill)

        coVerify(exactly = 1) {
            billRepository.update(match { it.isPaid })
        }
    }

    @Test
    fun `pagamento bem-sucedido deve inserir transação de pagamento`() = runTest {
        coEvery { transactionRepository.save(any()) } returns 1L

        val bill = buildBill(amount = 50_000L, isPaid = false)
        val account = buildAccount(balance = 100_000L)

        useCase(account, bill)

        coVerify(exactly = 1) {
            transactionRepository.save(match { it.amount == 50_000L })
        }
    }

    @Test
    fun `pagamento com saldo exatamente igual ao valor deve ter sucesso`() = runTest {
        coEvery { transactionRepository.save(any()) } returns 1L

        val bill = buildBill(amount = 100_000L, isPaid = false)
        val account = buildAccount(balance = 100_000L)

        val result = useCase(account, bill)

        assertIs<UseCaseResult.Success<Account>>(result)
        assertEquals(0L, result.data.balance)
    }
}
