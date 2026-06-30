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
import com.finsim.app.domain.repository.StockHoldingRepository
import com.finsim.app.domain.repository.StockPriceHistoryRepository
import com.finsim.app.domain.repository.StockPriceRepository
import com.finsim.app.domain.repository.UserAchievementRepository
import com.finsim.app.domain.repository.UserMissionRepository
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
    private val userMissionRepository: UserMissionRepository = mockk(relaxed = true)
    private val userAchievementRepository: UserAchievementRepository = mockk(relaxed = true)
    private val stockPriceRepository: StockPriceRepository = mockk(relaxed = true)
    private val stockHoldingRepository: StockHoldingRepository = mockk(relaxed = true)
    private val stockPriceHistoryRepository: StockPriceHistoryRepository = mockk(relaxed = true)

    private val useCase = AdvanceMonthUseCase(
        userProfileRepository = userProfileRepository,
        accountRepository = accountRepository,
        investmentRepository = investmentRepository,
        billRepository = billRepository,
        transactionRepository = transactionRepository,
        snapshotRepository = snapshotRepository,
        userMissionRepository = userMissionRepository,
        userAchievementRepository = userAchievementRepository,
        stockPriceRepository = stockPriceRepository,
        stockHoldingRepository = stockHoldingRepository,
        stockPriceHistoryRepository = stockPriceHistoryRepository,
    )

    private fun buildProfile(currentMonth: Int = 1) = UserProfile(
        id = 1L,
        name = "Alice",
        ageRange = AgeRange.TEEN,
        monthlyIncome = 200_000L,
        currentMonth = currentMonth,
        createdAt = 0L,
    )

    private fun buildAccount(balance: Long = 0L, reserve: Long = 0L) = Account(
        id = 1L,
        profileId = 1L,
        balance = balance,
        emergencyReserveBalance = reserve,
        updatedAt = 0L,
    )

    private fun buildBill(id: Long, amount: Long, isPaid: Boolean = false) = Bill(
        id = id,
        profileId = 1L,
        name = "Aluguel",
        amount = amount,
        month = 1,
        isPaid = isPaid,
        category = BillCategory.HOUSING,
        dueMonth = 1,
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
        createdAt = 0L,
    )

    private fun setupMocks(
        profile: UserProfile = buildProfile(),
        account: Account = buildAccount(),
        bills: List<Bill> = emptyList(),
        investments: List<FixedIncomeInvestment> = emptyList(),
    ) {
        coEvery { userProfileRepository.getById(1L) } returns profile
        coEvery { accountRepository.getByProfileId(1L) } returns flowOf(account)
        coEvery { investmentRepository.getByProfileId(1L) } returns flowOf(investments)
        coEvery { billRepository.getByProfileIdAndMonth(1L, profile.currentMonth) } returns flowOf(bills)
        coEvery { transactionRepository.save(any()) } returns 1L
        coEvery { snapshotRepository.save(any()) } returns 1L
        coEvery { userMissionRepository.getByProfileId(1L) } returns flowOf(emptyList())
        coEvery { userAchievementRepository.getByProfileId(1L) } returns flowOf(emptyList())
        coEvery { stockPriceRepository.getAll() } returns flowOf(emptyList())
        coEvery { stockHoldingRepository.getByProfileId(1L) } returns flowOf(emptyList())
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

        assertEquals(2, result.data.snapshot.month)
    }

    @Test
    fun deve_creditar_renda_mensal_na_conta() = runTest {
        val account = buildAccount(balance = 50_000L)
        setupMocks(account = account)

        useCase(profileId = 1L)

        coVerify(exactly = 1) { accountRepository.update(any()) }
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
    fun snapshot_deve_ser_salvo_apos_conta_atualizada() = runTest {
        setupMocks()

        useCase(profileId = 1L)

        coVerifyOrder {
            accountRepository.update(any())
            snapshotRepository.save(any())
        }
    }

    @Test
    fun contas_do_mes_atual_devem_ser_replicadas_para_o_proximo_mes() = runTest {
        val bills = listOf(
            buildBill(id = 1L, amount = 50_000L),
            buildBill(id = 2L, amount = 30_000L),
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

        assert(threw) { "Deveria ter lançado IllegalStateException para perfil inexistente" }
    }

    @Test
    fun fixedIncomeBalance_no_snapshot_deve_incluir_rendimento() = runTest {
        val investment = buildInvestment(currentAmount = 100_000L)
        setupMocks(investments = listOf(investment))

        val result = useCase(profileId = 1L) as UseCaseResult.Success

        assert(result.data.snapshot.fixedIncomeBalance >= 100_000L) {
            "Saldo de renda fixa deve incluir rendimento: obtido ${result.data.snapshot.fixedIncomeBalance}"
        }
    }

    @Test
    fun deve_retornar_resultado_com_campo_snapshot() = runTest {
        setupMocks()

        val result = useCase(profileId = 1L) as UseCaseResult.Success

        assertIs<AdvanceMonthResult>(result.data)
    }

    @Test
    fun resultado_deve_conter_listas_de_missoes_e_conquistas() = runTest {
        setupMocks()

        val result = useCase(profileId = 1L) as UseCaseResult.Success

        assertIs<List<*>>(result.data.newlyCompletedMissions)
        assertIs<List<*>>(result.data.newlyUnlockedAchievements)
    }
}
