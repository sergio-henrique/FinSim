package com.finsim.app.simulation.economy

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.Bill
import com.finsim.app.domain.model.FixedIncomeInvestment
import com.finsim.app.domain.model.RandomEvent
import com.finsim.app.domain.model.Transaction
import com.finsim.app.domain.model.TransactionType
import com.finsim.app.domain.model.UserProfile
import com.finsim.app.simulation.events.RandomEventEngine
import com.finsim.app.simulation.fixedincome.FixedIncomeEngine
import com.finsim.app.simulation.fixedincome.FixedIncomeResult
import com.finsim.app.simulation.fixedincome.SimulatedRates
import java.math.RoundingMode
import kotlin.random.Random

/**
 * Motor de passagem de mês na simulação financeira.
 *
 * Conceito pedagógico — ciclo financeiro mensal:
 * Todo mês, eventos acontecem em uma ordem determinada: os investimentos
 * rendem, a renda entra na conta, as contas chegam (já com inflação) e
 * imprevistos podem surgir. Simular essa ordem ensina que o tempo tem valor
 * e que cada decisão financeira impacta o próximo ciclo.
 *
 * Ordem de processamento (RN-010):
 *   1. Aplicar rendimento nos investimentos ativos.
 *   2. Depositar renda mensal.
 *   3. Gerar contas do novo mês com inflação aplicada.
 *   4. Gerar evento aleatório (se ocorrer).
 *   5. Retornar todos os resultados sem persistir nada.
 */
object MonthAdvanceEngine {

    data class MonthAdvanceInput(
        val profile: UserProfile,
        val account: Account,
        val activeInvestments: List<FixedIncomeInvestment>,
        val defaultBillTemplates: List<Bill>,
        val monthlyInflationRate: Double = SimulatedRates.MONTHLY_INFLATION,
        val random: Random = Random,
    )

    data class MonthAdvanceResult(
        val updatedInvestments: List<FixedIncomeInvestment>,
        val investmentResults: List<FixedIncomeResult>,
        val updatedAccount: Account,
        val newBills: List<Bill>,
        val newMonth: Int,
        val incomeTransaction: Transaction,
        val randomEvent: RandomEvent?,
    )

    fun advance(input: MonthAdvanceInput): MonthAdvanceResult {
        val nextMonth = input.profile.currentMonth + 1

        // 1. Rendimento dos investimentos
        val (updatedInvestments, investmentResults) = applyInvestmentReturns(input.activeInvestments)

        // 2. Crédito da renda mensal
        val incomeTransaction = Transaction(
            accountId = input.account.id,
            type = TransactionType.INCOME,
            amount = input.profile.monthlyIncome,
            description = "Renda mensal",
            month = nextMonth
        )
        val accountAfterIncome = input.account.copy(
            balance = input.account.balance + input.profile.monthlyIncome
        )

        // 3. Contas do novo mês com inflação aplicada
        val newBills = input.defaultBillTemplates.map { template ->
            val inflatedAmount = InflationEngine.applyMonthly(template.amount, input.monthlyInflationRate)
            template.copy(
                id = 0L,
                month = nextMonth,
                isPaid = false,
                dueMonth = nextMonth,
                amount = inflatedAmount,
            )
        }

        // 4. Evento aleatório
        val randomEvent = RandomEventEngine.generate(nextMonth, input.random)

        return MonthAdvanceResult(
            updatedInvestments = updatedInvestments,
            investmentResults = investmentResults,
            updatedAccount = accountAfterIncome,
            newBills = newBills,
            newMonth = nextMonth,
            incomeTransaction = incomeTransaction,
            randomEvent = randomEvent,
        )
    }

    private fun applyInvestmentReturns(
        investments: List<FixedIncomeInvestment>,
    ): Pair<List<FixedIncomeInvestment>, List<FixedIncomeResult>> {
        val updatedInvestments = mutableListOf<FixedIncomeInvestment>()
        val results = mutableListOf<FixedIncomeResult>()

        for (investment in investments) {
            val newAmount = FixedIncomeEngine.applyMonthlyReturn(
                currentAmountCents = investment.currentAmount,
                monthlyRate = investment.monthlyRate
            )
            val earnings = newAmount - investment.currentAmount

            updatedInvestments.add(investment.copy(currentAmount = newAmount))
            results.add(
                FixedIncomeResult(
                    previousAmountCents = investment.currentAmount,
                    newAmountCents = newAmount,
                    earningsCents = earnings,
                    monthlyRate = investment.monthlyRate,
                    productName = investment.productType.name,
                    educationalMessage = buildEarningsMessage(earnings, investment.monthlyRate)
                )
            )
        }

        return Pair(updatedInvestments, results)
    }

    private fun buildEarningsMessage(earningsCents: Long, rate: Double): String {
        val percent = (rate * 100)
            .toBigDecimal()
            .setScale(1, RoundingMode.HALF_UP)
        return "Seu investimento rendeu $percent% este mês. " +
            "Com juros compostos, quanto mais tempo você mantiver, mais ele cresce."
    }
}
