package com.finsim.app.domain.rule

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.Bill

/**
 * Regras de negócio puras da camada domain.
 *
 * Todas as funções são puras (sem efeito colateral) e testáveis sem Android.
 * Nenhuma dependência de framework é permitida neste objeto.
 *
 * Referência: docs/06_REGRAS_DE_NEGOCIO.md
 */
object FinancialRules {

    /**
     * RN-002: O pagamento de uma conta só pode ocorrer se o saldo livre
     * da conta corrente for maior ou igual ao valor da despesa.
     *
     * Prevenção: evita saldo negativo, ensinando ao usuário que não se
     * gasta o que não se tem.
     */
    fun canPayBill(account: Account, bill: Bill): Boolean =
        account.balance >= bill.amount

    /**
     * RN-003: Uma conta já paga não pode ser paga novamente.
     *
     * Prevenção: duplicidade de débito e inconsistência no histórico.
     */
    fun isBillAlreadyPaid(bill: Bill): Boolean = bill.isPaid

    /**
     * RN-005: A transferência de saldo livre para a reserva de emergência
     * só é permitida se o saldo livre for suficiente e o valor for positivo.
     *
     * Prevenção: reserva negativa ou transferência de valor zero sem sentido.
     */
    fun canTransferToReserve(account: Account, amount: Long): Boolean =
        account.balance >= amount && amount > 0

    /**
     * RN-007: A aplicação em renda fixa só é permitida se o saldo livre
     * for suficiente e o valor aplicado for positivo.
     *
     * Prevenção: aplicação sem fundos disponíveis; ensina que investir
     * exige planejamento prévio.
     */
    fun canInvest(account: Account, amount: Long): Boolean =
        account.balance >= amount && amount > 0

    /**
     * RN-001: O saldo da conta nunca pode ser negativo.
     *
     * Usado em validações de consistência pós-operação para detectar
     * bugs de lógica antes de persistir o estado.
     */
    fun isBalanceConsistent(balance: Long): Boolean = balance >= 0

    /**
     * Calcula a nota de saúde financeira do usuário no mês simulado (0 a 100).
     *
     * Critérios e pesos:
     * - Contas pagas no mês:      até 40 pontos (proporcional ao total devido).
     * - Reserva de emergência:    30 pontos (binário: tem ou não tem).
     * - Algum investimento ativo: 20 pontos (binário).
     * - Saldo livre positivo:     10 pontos (binário).
     *
     * A nota serve apenas como feedback educativo e não representa
     * nenhuma avaliação de crédito real.
     *
     * @param billsPaid      Soma em centavos das contas efetivamente pagas.
     * @param billsTotal     Soma em centavos de todas as contas do mês.
     * @param hasReserve     true se a reserva de emergência tiver saldo > 0.
     * @param hasInvestment  true se houver pelo menos um investimento ativo.
     * @param isBalancePositive true se o saldo livre for > 0 ao final do mês.
     * @return Inteiro entre 0 e 100 inclusive.
     */
    fun calculateHealthScore(
        billsPaid: Long,
        billsTotal: Long,
        hasReserve: Boolean,
        hasInvestment: Boolean,
        isBalancePositive: Boolean
    ): Int {
        var score = 0

        if (billsTotal > 0 && billsPaid >= billsTotal) {
            score += 40
        } else if (billsTotal > 0) {
            score += ((billsPaid.toDouble() / billsTotal) * 40).toInt()
        }

        if (hasReserve) score += 30
        if (hasInvestment) score += 20
        if (isBalancePositive) score += 10

        return score.coerceIn(0, 100)
    }
}
