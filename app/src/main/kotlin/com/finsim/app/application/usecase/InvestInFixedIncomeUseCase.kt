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
 * Caso de uso: Aplicar em renda fixa simulada (Tesouro Selic ou CDB simulado).
 *
 * Conceito pedagógico — risco × retorno:
 * O CDB paga mais que o Tesouro Selic, mas depende da saúde do banco emissor.
 * Essa diferença de taxa ajuda o educando a entender que rentabilidade maior
 * geralmente vem acompanhada de algum risco adicional.
 *
 * Aplica RN-007 (saldo suficiente) e RN-009 (produto padronizado com taxa e liquidez).
 */
class InvestInFixedIncomeUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val investmentRepository: FixedIncomeInvestmentRepository,
    private val transactionRepository: TransactionRepository,
) {

    /**
     * @param account      Conta corrente atual.
     * @param amount       Valor em centavos (> 0 e <= saldo).
     * @param profileId    Id do perfil.
     * @param currentMonth Mês simulado atual.
     * @param productType  Produto de renda fixa escolhido (padrão: Tesouro Selic).
     */
    suspend operator fun invoke(
        account: Account,
        amount: Long,
        profileId: Long,
        currentMonth: Int,
        productType: FixedIncomeProductType = FixedIncomeProductType.TESOURO_SELIC_SIMULADO,
    ): UseCaseResult<FixedIncomeInvestment> {
        if (!FinancialRules.canInvest(account, amount)) {
            return UseCaseResult.Failure(
                "Saldo insuficiente para investir. " +
                    "Invista apenas o que não vai precisar no curto prazo e mantenha sua reserva de emergência."
            )
        }

        val rateBps = rateBpsFor(productType)

        val investment = FixedIncomeInvestment(
            profileId = profileId,
            productType = productType,
            investedAmount = amount,
            currentAmount = amount,
            monthlyRateBps = rateBps,
            startMonth = currentMonth,
            maturityMonth = maturityMonthFor(productType),
            liquidityType = liquidityFor(productType),
            createdAt = System.currentTimeMillis()
        )
        val investmentId = investmentRepository.save(investment)
        val savedInvestment = investment.copy(id = investmentId)

        accountRepository.update(
            account.copy(
                balance = account.balance - amount,
                updatedAt = System.currentTimeMillis()
            )
        )

        transactionRepository.save(
            Transaction(
                accountId = account.id,
                type = TransactionType.INVESTMENT_APPLICATION,
                amount = amount,
                description = "Aplicação em ${productType.displayName}",
                month = currentMonth
            )
        )

        return UseCaseResult.Success(savedInvestment)
    }

    private fun rateBpsFor(productType: FixedIncomeProductType): Int = when (productType) {
        FixedIncomeProductType.TESOURO_SELIC_SIMULADO -> TESOURO_SELIC_BPS
        FixedIncomeProductType.CDB_SIMULADO -> CDB_BPS
    }

    private fun maturityMonthFor(productType: FixedIncomeProductType): Int? = when (productType) {
        FixedIncomeProductType.TESOURO_SELIC_SIMULADO -> null
        FixedIncomeProductType.CDB_SIMULADO -> null
    }

    private fun liquidityFor(productType: FixedIncomeProductType): LiquidityType = when (productType) {
        FixedIncomeProductType.TESOURO_SELIC_SIMULADO -> LiquidityType.DAILY
        FixedIncomeProductType.CDB_SIMULADO -> LiquidityType.DAILY
    }

    private companion object {
        const val TESOURO_SELIC_BPS = 80  // 0,80%/mês
        const val CDB_BPS = 90            // 0,90%/mês
    }
}

private val FixedIncomeProductType.displayName: String
    get() = when (this) {
        FixedIncomeProductType.TESOURO_SELIC_SIMULADO -> "Tesouro Selic Simulado"
        FixedIncomeProductType.CDB_SIMULADO -> "CDB Simulado"
    }
