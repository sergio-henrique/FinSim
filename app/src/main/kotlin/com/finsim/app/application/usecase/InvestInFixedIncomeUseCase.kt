package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.FixedIncomeInvestment
import com.finsim.app.domain.model.FixedIncomeProductType
import com.finsim.app.domain.model.LiquidityType
import com.finsim.app.domain.model.Transaction
import com.finsim.app.domain.model.TransactionType
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.FixedIncomeInvestmentRepository
import com.finsim.app.domain.repository.TransactionRepository
import com.finsim.app.domain.rule.FinancialRules
import javax.inject.Inject

/**
 * Caso de uso: Aplicar em renda fixa simulada (Tesouro Selic simulado).
 *
 * Aplica RN-007 (saldo suficiente para investir) e RN-009 (produto padronizado
 * com taxa e liquidez definidas).
 *
 * Conceito pedagógico: investir exige planejamento. O usuário aprende que só deve
 * aplicar o dinheiro de que não vai precisar no curto prazo, pois o investimento
 * ficará rendendo ao longo dos meses.
 *
 * Taxa usada: 80 bps/mês (0,80%/mês) — Tesouro Selic simulado.
 * Liquidez: diária (pode resgatar a qualquer mês).
 */
class InvestInFixedIncomeUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val investmentRepository: FixedIncomeInvestmentRepository,
    private val transactionRepository: TransactionRepository
) {

    /**
     * @param account      Conta corrente atual do usuário.
     * @param amount       Valor em centavos a aplicar (deve ser positivo e <= saldo).
     * @param profileId    Id do perfil para vincular o investimento.
     * @param currentMonth Mês simulado atual, usado como [FixedIncomeInvestment.startMonth].
     * @return [UseCaseResult.Success] com o investimento criado, ou
     *         [UseCaseResult.Failure] com mensagem educativa.
     */
    suspend operator fun invoke(
        account: Account,
        amount: Long,
        profileId: Long,
        currentMonth: Int
    ): UseCaseResult<FixedIncomeInvestment> {
        if (!FinancialRules.canInvest(account, amount)) {
            return UseCaseResult.Failure(
                "Saldo insuficiente para investir. " +
                    "Invista apenas o que não vai precisar no curto prazo e lembre-se de manter sua reserva de emergência."
            )
        }

        val investment = FixedIncomeInvestment(
            profileId = profileId,
            productType = FixedIncomeProductType.TESOURO_SELIC_SIMULADO,
            investedAmount = amount,
            currentAmount = amount,
            monthlyRateBps = TESOURO_SELIC_MONTHLY_RATE_BPS,
            startMonth = currentMonth,
            maturityMonth = null,
            liquidityType = LiquidityType.DAILY,
            createdAt = System.currentTimeMillis()
        )
        val investmentId = investmentRepository.save(investment)
        val savedInvestment = investment.copy(id = investmentId)

        val updatedAccount = account.copy(
            balance = account.balance - amount,
            updatedAt = System.currentTimeMillis()
        )
        accountRepository.update(updatedAccount)

        transactionRepository.save(
            Transaction(
                accountId = account.id,
                type = TransactionType.INVESTMENT_APPLICATION,
                amount = amount,
                description = "Aplicação em Tesouro Selic simulado",
                month = currentMonth
            )
        )

        return UseCaseResult.Success(savedInvestment)
    }

    private companion object {
        /**
         * 80 bps = 0,80%/mês — corresponde a [SimulatedRates.TESOURO_SELIC_MONTHLY] × 10 000.
         * Armazenado como Int em bps para evitar Double na persistência (RN de dinheiro).
         */
        const val TESOURO_SELIC_MONTHLY_RATE_BPS = 80
    }
}
