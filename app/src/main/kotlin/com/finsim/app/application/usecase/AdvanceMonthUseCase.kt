package com.finsim.app.application.usecase

import com.finsim.app.domain.model.MonthlySnapshot
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
 * Caso de uso: Avançar o mês simulado.
 *
 * Implementa a ordem obrigatória definida em RN-010:
 *   1. Rendimento dos investimentos.
 *   2. Crédito da renda mensal.
 *   3. Geração das contas do novo mês.
 *   4. Persistência de todos os novos estados.
 *   5. Cálculo do score de saúde financeira.
 *   6. Registro do snapshot mensal.
 *
 * A ordem de rendimento antes da renda é pedagogicamente intencional:
 * demonstra que o capital investido trabalha independentemente do salário.
 *
 * Conceito pedagógico: cada mês que passa sem investir é uma oportunidade
 * perdida de fazer o dinheiro trabalhar por você.
 *
 * @throws IllegalStateException se o perfil ou conta não forem encontrados.
 */
class AdvanceMonthUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val accountRepository: AccountRepository,
    private val investmentRepository: FixedIncomeInvestmentRepository,
    private val billRepository: BillRepository,
    private val transactionRepository: TransactionRepository,
    private val snapshotRepository: MonthlySnapshotRepository
) {

    /**
     * @param profileId Id do perfil cujo mês será avançado.
     * @return [UseCaseResult.Success] com o [MonthlySnapshot] do mês encerrado, ou
     *         [UseCaseResult.Failure] em caso de erro inesperado.
     */
    suspend operator fun invoke(profileId: Long): UseCaseResult<MonthlySnapshot> {
        // Etapa 1: Carregar estado atual
        val profile = userProfileRepository.getById(profileId)
            ?: error("Perfil não encontrado para id=$profileId")

        val account = accountRepository.getByProfileId(profileId).first()
            ?: error("Conta não encontrada para profileId=$profileId")

        val activeInvestments = investmentRepository.getByProfileId(profileId).first()

        // Etapa 2: Buscar contas do mês atual como templates para o próximo
        val currentMonthBills = billRepository
            .getByProfileIdAndMonth(profileId, profile.currentMonth)
            .first()

        // Etapa 3: Processar passagem de mês via motor de simulação
        val input = MonthAdvanceEngine.MonthAdvanceInput(
            profile = profile,
            account = account,
            activeInvestments = activeInvestments,
            defaultBillTemplates = currentMonthBills
        )
        val result = MonthAdvanceEngine.advance(input)

        // Etapa 4a: Persistir investimentos com rendimento aplicado
        result.updatedInvestments.forEach { investmentRepository.update(it) }

        // Etapa 4b: Persistir conta com renda creditada
        accountRepository.update(result.updatedAccount)

        // Etapa 4c: Persistir transação de renda
        transactionRepository.save(result.incomeTransaction)

        // Etapa 4d: Persistir contas geradas para o novo mês
        result.newBills.forEach { billRepository.save(it) }

        // Etapa 5: Atualizar mês corrente no perfil
        val updatedProfile = profile.copy(currentMonth = result.newMonth)
        userProfileRepository.update(updatedProfile)

        // Etapa 6: Calcular score de saúde financeira com base no mês encerrado
        val billsPaid = currentMonthBills.filter { it.isPaid }.sumOf { it.amount }
        val billsTotal = currentMonthBills.sumOf { it.amount }
        val fixedIncomeBalance = result.updatedInvestments.sumOf { it.currentAmount }

        val healthScore = FinancialRules.calculateHealthScore(
            billsPaid = billsPaid,
            billsTotal = billsTotal,
            hasReserve = result.updatedAccount.emergencyReserveBalance > 0,
            hasInvestment = result.updatedInvestments.isNotEmpty(),
            isBalancePositive = result.updatedAccount.balance > 0
        )

        // Etapa 7: Registrar snapshot do mês encerrado
        val snapshot = MonthlySnapshot(
            profileId = profileId,
            month = profile.currentMonth,
            accountBalance = result.updatedAccount.balance,
            reserveBalance = result.updatedAccount.emergencyReserveBalance,
            fixedIncomeBalance = fixedIncomeBalance,
            totalWealth = result.updatedAccount.balance +
                result.updatedAccount.emergencyReserveBalance +
                fixedIncomeBalance,
            billsPaidAmount = billsPaid,
            billsPendingAmount = billsTotal - billsPaid,
            financialHealthScore = healthScore
        )
        snapshotRepository.save(snapshot)

        return UseCaseResult.Success(snapshot)
    }
}
