package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Achievement
import com.finsim.app.domain.model.MonthlySnapshot
import com.finsim.app.domain.model.RandomEvent
import com.finsim.app.domain.model.Transaction
import com.finsim.app.domain.model.TransactionType
import com.finsim.app.domain.model.UserAchievementRecord
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.BillRepository
import com.finsim.app.domain.repository.FixedIncomeInvestmentRepository
import com.finsim.app.domain.repository.MonthlySnapshotRepository
import com.finsim.app.domain.repository.TransactionRepository
import com.finsim.app.domain.repository.UserAchievementRepository
import com.finsim.app.domain.repository.UserMissionRepository
import com.finsim.app.domain.repository.UserProfileRepository
import com.finsim.app.domain.rule.FinancialRules
import com.finsim.app.simulation.economy.MonthAdvanceEngine
import com.finsim.app.simulation.missions.AchievementEngine
import com.finsim.app.simulation.missions.MissionEngine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Resultado enriquecido da passagem de mês — inclui o snapshot, o evento
 * aleatório, missões recém-concluídas e conquistas desbloqueadas.
 */
data class AdvanceMonthResult(
    val snapshot: MonthlySnapshot,
    val randomEvent: RandomEvent?,
    val newlyCompletedMissions: List<String>,
    val newlyUnlockedAchievements: List<Achievement>,
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
 *   8. Avaliação de missões e conquistas.
 */
class AdvanceMonthUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val accountRepository: AccountRepository,
    private val investmentRepository: FixedIncomeInvestmentRepository,
    private val billRepository: BillRepository,
    private val transactionRepository: TransactionRepository,
    private val snapshotRepository: MonthlySnapshotRepository,
    private val userMissionRepository: UserMissionRepository,
    private val userAchievementRepository: UserAchievementRepository,
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
        val reserveBeforeEvent = finalAccount.emergencyReserveBalance

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
        val allBillsPaid = billsTotal > 0 && billsPaid >= billsTotal

        val healthScore = FinancialRules.calculateHealthScore(
            billsPaid = billsPaid,
            billsTotal = billsTotal,
            hasReserve = finalAccount.emergencyReserveBalance > 0,
            hasInvestment = engineResult.updatedInvestments.isNotEmpty(),
            isBalancePositive = finalAccount.balance > 0,
        )

        val totalWealth = finalAccount.balance + finalAccount.emergencyReserveBalance + fixedIncomeBalance
        val snapshot = MonthlySnapshot(
            profileId = profileId,
            month = profile.currentMonth,
            accountBalance = finalAccount.balance,
            reserveBalance = finalAccount.emergencyReserveBalance,
            fixedIncomeBalance = fixedIncomeBalance,
            totalWealth = totalWealth,
            billsPaidAmount = billsPaid,
            billsPendingAmount = billsTotal - billsPaid,
            financialHealthScore = healthScore,
        )
        snapshotRepository.save(snapshot)

        // Avaliação de missões
        val existingProgress = userMissionRepository.getByProfileId(profileId).first()
        val missionState = MissionEngine.SimulationState(
            profileId = profileId,
            currentMonth = engineResult.newMonth,
            paidBillsCount = currentMonthBills.count { it.isPaid },
            totalBillsCount = currentMonthBills.size,
            reserveBalanceCents = finalAccount.emergencyReserveBalance,
            fixedIncomeBalanceCents = fixedIncomeBalance,
            hasAnyInvestment = engineResult.updatedInvestments.isNotEmpty(),
        )
        val missionUpdates = MissionEngine.evaluate(existingProgress, missionState)
        missionUpdates.forEach { updated ->
            val existing = existingProgress.find { it.missionId == updated.missionId }
            if (existing == null) {
                userMissionRepository.saveAll(listOf(updated))
            } else {
                userMissionRepository.update(updated.copy(id = existing.id))
            }
        }
        val newlyCompleted = MissionEngine.newlyCompleted(missionUpdates, engineResult.newMonth)

        // Avaliação de conquistas
        val existingAchievements = userAchievementRepository.getByProfileId(profileId).first()
        val unlockedIds = existingAchievements.map { it.achievementId }.toSet()

        val survivedWithReserve = randomEvent != null &&
                finalAccount.emergencyReserveBalance >= reserveBeforeEvent

        val achievementContext = AchievementEngine.AchievementContext(
            profileId = profileId,
            currentMonth = engineResult.newMonth,
            totalWealthCents = totalWealth,
            reserveBalanceCents = finalAccount.emergencyReserveBalance,
            hasAnyInvestment = engineResult.updatedInvestments.isNotEmpty(),
            allBillsPaidThisMonth = allBillsPaid,
            survivedEventWithReserveIntact = survivedWithReserve,
            newlyCompletedMissions = newlyCompleted,
        )
        val newAchievements = AchievementEngine.evaluate(achievementContext, unlockedIds)
        newAchievements.forEach { achievement ->
            userAchievementRepository.save(
                UserAchievementRecord(
                    profileId = profileId,
                    achievementId = achievement.id,
                    unlockedMonth = engineResult.newMonth,
                )
            )
        }

        return UseCaseResult.Success(
            AdvanceMonthResult(
                snapshot = snapshot,
                randomEvent = randomEvent,
                newlyCompletedMissions = newlyCompleted.map { it.missionId },
                newlyUnlockedAchievements = newAchievements,
            )
        )
    }
}
