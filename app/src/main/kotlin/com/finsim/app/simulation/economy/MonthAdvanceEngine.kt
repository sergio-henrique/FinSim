package com.finsim.app.simulation.economy

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.Bill
import com.finsim.app.domain.model.FixedIncomeInvestment
import com.finsim.app.domain.model.Transaction
import com.finsim.app.domain.model.TransactionType
import com.finsim.app.domain.model.UserProfile
import com.finsim.app.simulation.fixedincome.FixedIncomeEngine
import com.finsim.app.simulation.fixedincome.FixedIncomeResult
import java.math.RoundingMode

/**
 * Motor de passagem de mês na simulação financeira.
 *
 * Conceito pedagógico — ciclo financeiro mensal:
 * Todo mês, eventos acontecem em uma ordem determinada: os investimentos
 * rendem, a renda entra na conta e as contas chegam para pagar. Simular
 * essa ordem ensina que o tempo tem valor e que cada decisão financeira
 * tomada hoje impacta o próximo ciclo.
 *
 * Ordem de processamento (conforme RN-010):
 *   1. Aplicar rendimento nos investimentos ativos.
 *   2. Depositar renda mensal na conta corrente.
 *   3. Gerar as contas do novo mês a partir dos templates.
 *   4. Retornar todos os resultados sem persistir nada.
 *
 * Este motor NÃO acessa banco de dados. Recebe os dados já carregados,
 * processa e retorna os novos estados. A persistência é responsabilidade
 * da camada data, orquestrada pela camada application.
 */
object MonthAdvanceEngine {

    /**
     * Dados necessários para processar a passagem de um mês.
     *
     * @property profile              Perfil do usuário com renda e mês atual.
     * @property account              Conta corrente com saldo atual.
     * @property activeInvestments    Lista de investimentos em andamento.
     * @property defaultBillTemplates Modelos de contas recorrentes para gerar o novo mês.
     */
    data class MonthAdvanceInput(
        val profile: UserProfile,
        val account: Account,
        val activeInvestments: List<FixedIncomeInvestment>,
        val defaultBillTemplates: List<Bill>
    )

    /**
     * Resultado completo da passagem de mês — contém os novos estados
     * de todos os objetos afetados, prontos para persistência.
     *
     * @property updatedInvestments  Investimentos com saldo atualizado após rendimento.
     * @property investmentResults   Detalhe pedagógico de cada rendimento aplicado.
     * @property updatedAccount      Conta após crédito da renda mensal.
     * @property newBills            Contas geradas para o novo mês.
     * @property newMonth            Número do mês resultante da passagem.
     * @property incomeTransaction   Registro da transação de renda para histórico.
     */
    data class MonthAdvanceResult(
        val updatedInvestments: List<FixedIncomeInvestment>,
        val investmentResults: List<FixedIncomeResult>,
        val updatedAccount: Account,
        val newBills: List<Bill>,
        val newMonth: Int,
        val incomeTransaction: Transaction
    )

    /**
     * Processa a passagem de mês e retorna todos os novos estados.
     *
     * Nenhuma entrada é mutada — todos os objetos retornados são novas instâncias.
     *
     * @param input Dados do estado atual da simulação.
     * @return Novos estados de todos os objetos afetados.
     */
    fun advance(input: MonthAdvanceInput): MonthAdvanceResult {
        val nextMonth = input.profile.currentMonth + 1

        // Etapa 1: Aplicar rendimento mensal nos investimentos ativos.
        // Conceito: o dinheiro investido rende antes mesmo da renda entrar,
        // ilustrando que investimentos trabalham independentemente do usuário.
        val (updatedInvestments, investmentResults) = applyInvestmentReturns(
            investments = input.activeInvestments
        )

        // Etapa 2: Depositar renda mensal.
        // Conceito: a renda entra depois dos rendimentos para enfatizar
        // que o capital investido já estava crescendo enquanto isso.
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

        // Etapa 3: Gerar contas do novo mês a partir dos templates.
        // Conceito: despesas recorrentes lembram ao usuário que compromissos
        // financeiros chegam todo mês e precisam ser planejados.
        // id = 0 indica que a entidade ainda não foi persistida.
        val newBills = input.defaultBillTemplates.map { template ->
            template.copy(
                id = 0L,
                month = nextMonth,
                isPaid = false,
                dueMonth = nextMonth
            )
        }

        return MonthAdvanceResult(
            updatedInvestments = updatedInvestments,
            investmentResults = investmentResults,
            updatedAccount = accountAfterIncome,
            newBills = newBills,
            newMonth = nextMonth,
            incomeTransaction = incomeTransaction
        )
    }

    /**
     * Aplica o rendimento mensal em cada investimento da lista.
     *
     * Retorna um par: a lista de investimentos atualizados e a lista de
     * resultados pedagógicos correspondentes, na mesma ordem.
     */
    private fun applyInvestmentReturns(
        investments: List<FixedIncomeInvestment>
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
                    educationalMessage = buildEducationalMessage(earnings, investment.monthlyRate)
                )
            )
        }

        return Pair(updatedInvestments, results)
    }

    /**
     * Constrói a mensagem pedagógica sobre o rendimento do mês.
     *
     * A mensagem é formatada de forma simples para ser compreensível
     * por adolescentes, reforçando o conceito de juros compostos.
     *
     * @param earningsCents Rendimento do mês em centavos.
     * @param rate          Taxa mensal aplicada em decimal.
     * @return Texto educativo sobre o rendimento.
     */
    private fun buildEducationalMessage(earningsCents: Long, rate: Double): String {
        val percent = (rate * 100)
            .toBigDecimal()
            .setScale(1, RoundingMode.HALF_UP)
        return "Seu investimento rendeu $percent% este mês. " +
            "Com juros compostos, quanto mais tempo você mantiver, mais ele cresce."
    }
}
