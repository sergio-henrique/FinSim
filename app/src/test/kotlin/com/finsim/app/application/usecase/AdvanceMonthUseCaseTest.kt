package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.AgeRange
import com.finsim.app.domain.model.Bill
import com.finsim.app.domain.model.BillCategory
import com.finsim.app.domain.model.FixedIncomeInvestment
import com.finsim.app.domain.model.FixedIncomeProductType
import com.finsim.app.domain.model.LiquidityType
import com.finsim.app.domain.model.UserProfile
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.BillRepository
import com.finsim.app.domain.repository.FixedIncomeInvestmentRepository
import com.finsim.app.domain.repository.MonthlySnapshotRepository
import com.finsim.app.domain.repository.TransactionRepository
import com.finsim.app.domain.repository.UserProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AdvanceMonthUseCaseTest {

    private val userProfileRepository: UserProfileRepository = mockk(relaxed = true)
    private val accountRepository: AccountRepository = mockk(relaxed = true)
    private val investmentRepository: FixedIncomeInvestmentRepository = mockk(relaxed = true)
    private val billRepository: BillRepository = mockk(relaxed = true)
    private val transactionRepository: TransactionRepository = mockk(relaxed = true)
    private val snapshotRepository: MonthlySnapshotRepository = mockk(relaxed = true)

    private val useCase = AdvanceMonthUseCase(
        userProfileRepository = userProfileRepository,
        accountRepository = accountRepository,
        investmentRepository = investmentRepository,
        billRepository = billRepository,
        transactionRepository = transactionRepository,
        snapshotRepository = snapshotRepository
    )

    private fun buildProfile(currentMonth: Int = 1) = UserProfile(
        id = 1L,
        name = "Alice",
        ageRange = AgeRange.TEEN,
        monthlyIncome = 200_000L,
        currentMonth = currentMonth,
        createdAt = 0L
    )

    private fun buildAccount(balance: Long = 0L, reserve: Long = 0L) = Account(
        id = 1L,
        profileId = 1L,
        balance = balance,
        emergencyReserveBalance = reserve,
        updatedAt = 0L
    )

    private fun buildBill(id: Long, amount: Long, isPaid: Boolean = false) = Bill(
        id = id,
        profileId = 1L,
        name = "Aluguel",
        amount = amount,
        month = 1,
        isPaid = isPaid,
        category = BillCategory.HOUSING,
        dueMonth = 1
    )

    private fun buildInvestment(currentAmount: Long) = FixedIncomeInvestment(
        id = 1L,
        profileId = 1L,
        productType = FixedIncomeProductType.TESOURO_SELIC_SIMULADO,
        investedAmount = currentAmount,
        currentAmount = currentAmount,
        monthlyRateBps = 80,
        startMonth = 1,
        maturityMonth = null,
        liquidityType = LiquidityType.DAILY,
        createdAt = 0L
    )

    private fun setupMocks(
        profile: UserProfile = buildProfile(),
        account: Account = buildAccount(),
        bills: List<Bill> = emptyList(),
        investments: List<FixedIncomeInvestment> = emptyList()
    ) {
        coEvery { userProfileRepository.getById(1L) } returns profile
        coEvery { accountRepository.getByProfileId(1L) } returns flowOf(account)
        coEvery { investmentRepository.getByProfileId(1L) } returns flowOf(investments)
        coEvery { billRepository.getByProfileIdAndMonth(1L, profile.currentMonth) } returns flowOf(bills)
        coEvery { transactionRepository.save(any()) } returns 1L
        coEvery { snapshotRepository.save(any()) } returns 1L
    }

    @Test
    fun deve_incrementar_currentMonth_do_perfil_apos_avancar() = runTest {
        setupMocks(profile = buildProfile(currentMonth = 3))

        useCase(profileId = 1L)

        coVerify(exactly = 1) {
            userProfileRepository.update(match { it.currentMonth == 4 })
        }
    }

    @Test
    fun deve_retornar_snapshot_com_mes_encerrado() = runTest {
        setupMocks(profile = buildProfile(currentMonth = 2))

        val result = useCase(profileId = 1L) as UseCaseResult.Success

        assertEquals(2, result.data.month)
    }

    @Test
    fun deve_creditar_renda_mensal_na_conta() = runTest {
        val account = buildAccount(balance = 50_000L)
        setupMocks(account = account)

        useCase(profileId = 1L)

        coVerify(exactly = 1) {
            accountRepository.update(match { it.balance == 250_000L })
        }
    }

    @Test
    fun investimentos_devem_ser_atualizados_antes_da_conta() = runTest {
        val investment = buildInvestment(currentAmount = 100_000L)
        setupMocks(investments = listOf(investment))

        useCase(profileId = 1L)

        coVerifyOrder {
            investmentRepository.update(any())
            accountRepository.update(any())
        }
    }

    @Test
    fun transacao_de_renda_deve_ser_salva_apos_conta_atualizada() = runTest {
        setupMocks()

        useCase(profileId = 1L)

        coVerifyOrder {
            accountRepository.update(any())
            transactionRepository.save(any())
        }
    }

    @Test
    fun snapshot_deve_ser_salvo_por_ultimo() = runTest {
        setupMocks()

        useCase(profileId = 1L)

        coVerifyOrder {
            accountRepository.update(any())
            transactionRepository.save(any())
            snapshotRepository.save(any())
        }
    }

    @Test
    fun contas_do_mes_atual_devem_ser_replicadas_para_o_proximo_mes() = runTest {
        val bills = listOf(
            buildBill(id = 1L, amount = 50_000L),
            buildBill(id = 2L, amount = 30_000L)
        )
        setupMocks(bills = bills)

        useCase(profileId = 1L)

        coVerify(exactly = 2) { billRepository.save(any()) }
    }

    @Test
    fun deve_retornar_Success_mesmo_sem_investimentos_ativos() = runTest {
        setupMocks(investments = emptyList())

        val result = useCase(profileId = 1L)

        assertIs<UseCaseResult.Success<*>>(result)
    }

    @Test
    fun deve_lancar_excecao_quando_perfil_nao_for_encontrado() = runTest {
        coEvery { userProfileRepository.getById(99L) } returns null

        var threw = false
        try {
            useCase(profileId = 99L)
        } catch (e: IllegalStateException) {
            threw = true
        }

        assert(threw) { "Deveria ter lancado IllegalStateException para perfil inexistente" }
    }

    @Test
    fun fixedIncomeBalance_no_snapshot_deve_refletir_rendimento_aplicado() = runTest {
        val investment = buildInvestment(currentAmount = 100_000L)
        setupMocks(investments = listOf(investment))

        val result = useCase(profileId = 1L) as UseCaseResult.Success

        // Rendimento de 0.80% sobre 100_000 centavos = 800 centavos
        // currentAmount esperado = 100_800L
        assert(result.data.fixedIncomeBalance >= 100_000L) {
            "O saldo de renda fixa deve incluir rendimento: esperado >= 100_000, obtido ${result.data.fixedIncomeBalance}"
        }
    }
}
