package com.finsim.app.application.usecase

import com.finsim.app.domain.model.MonthlySnapshot
import com.finsim.app.domain.model.RandomEvent
import com.finsim.app.domain.model.Transaction
import com.finsim.app.domain.model.TransactionType
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.BillRepository
import com.finsim.app.domain.repository.FixedIncomeInvestmentRepository
import com.finsim.app.domain.repository.MonthlySnapshotRepository
import com.finsim.app.domain.repository.TransactionRepository
import com.finsim.app.domain.repository.UserProfileRepository
import com.finsim.app.domain.rule.FinancialRules
import com.finsim.app.simulation.economy.MonthAdvanceEngine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Resultado enriquecido da passagem de mês — inclui o snapshot e o evento
 * aleatório (se ocorreu) para exibição na UI.
 */
data class AdvanceMonthResult(
    val snapshot: MonthlySnapshot,
    val randomEvent: RandomEvent?,
)

/**
 * Caso de uso: Avançar o mês simulado.
 *
 * Ordem de processamento (RN-010):
 *   1. Rendimento dos investimentos.
 *   2. Crédito da renda mensal.
 *   3. Geração das contas com inflação.
 *   4. Evento financeiro aleatório.
 *   5. Persistência de todos os estados.
 *   6. Score de saúde financeira.
 *   7. Snapshot mensal.
 */
class AdvanceMonthUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val accountRepository: AccountRepository,
    private val investmentRepository: FixedIncomeInvestmentRepository,
    private val billRepository: BillRepository,
    private val transactionRepository: TransactionRepository,
    private val snapshotRepository: MonthlySnapshotRepository,
) {

    suspend operator fun invoke(profileId: Long): UseCaseResult<AdvanceMonthResult> {
        val profile = userProfileRepository.getById(profileId)
            ?: error("Perfil não encontrado para id=$profileId")

        val account = accountRepository.getByProfileId(profileId).first()
            ?: error("Conta não encontrada para profileId=$profileId")

        val activeInvestments = investmentRepository.getByProfileId(profileId).first()

        val currentMonthBills = billRepository
            .getByProfileIdAndMonth(profileId, profile.currentMonth)
            .first()

        val engineResult = MonthAdvanceEngine.advance(
            MonthAdvanceEngine.MonthAdvanceInput(
                profile = profile,
                account = account,
                activeInvestments = activeInvestments,
                defaultBillTemplates = currentMonthBills,
            )
        )

        // Persistência: investimentos
        engineResult.updatedInvestments.forEach { investmentRepository.update(it) }

        // Persistência: conta (com possível débito do evento)
        var finalAccount = engineResult.updatedAccount
        val randomEvent = engineResult.randomEvent

        if (randomEvent != null) {
            val eventDebit = randomEvent.amountCents.coerceAtMost(finalAccount.balance)
            finalAccount = finalAccount.copy(balance = finalAccount.balance - eventDebit)

            transactionRepository.save(
                Transaction(
                    accountId = account.id,
                    type = TransactionType.EVENT,
                    amount = eventDebit,
                    description = randomEvent.title,
                    month = engineResult.newMonth,
                )
            )
        }

        accountRepository.update(finalAccount)
        transactionRepository.save(engineResult.incomeTransaction)
        engineResult.newBills.forEach { billRepository.save(it) }

        val updatedProfile = profile.copy(currentMonth = engineResult.newMonth)
        userProfileRepository.update(updatedProfile)

        // Score de saúde financeira
        val billsPaid = currentMonthBills.filter { it.isPaid }.sumOf { it.amount }
        val billsTotal = currentMonthBills.sumOf { it.amount }
        val fixedIncomeBalance = engineResult.updatedInvestments.sumOf { it.currentAmount }

        val healthScore = FinancialRules.calculateHealthScore(
            billsPaid = billsPaid,
            billsTotal = billsTotal,
            hasReserve = finalAccount.emergencyReserveBalance > 0,
            hasInvestment = engineResult.updatedInvestments.isNotEmpty(),
            isBalancePositive = finalAccount.balance > 0,
        )

        val snapshot = MonthlySnapshot(
            profileId = profileId,
            month = profile.currentMonth,
            accountBalance = finalAccount.balance,
            reserveBalance = finalAccount.emergencyReserveBalance,
            fixedIncomeBalance = fixedIncomeBalance,
            totalWealth = finalAccount.balance + finalAccount.emergencyReserveBalance + fixedIncomeBalance,
            billsPaidAmount = billsPaid,
            billsPendingAmount = billsTotal - billsPaid,
            financialHealthScore = healthScore,
        )
        snapshotRepository.save(snapshot)

        return UseCaseResult.Success(AdvanceMonthResult(snapshot, randomEvent))
    }
}
