package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.Bill
import com.finsim.app.domain.model.Transaction
import com.finsim.app.domain.model.TransactionType
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.BillRepository
import com.finsim.app.domain.repository.TransactionRepository
import com.finsim.app.domain.rule.FinancialRules
import javax.inject.Inject

/**
 * Caso de uso: Pagar uma conta do mês simulado.
 *
 * Aplica RN-002 (saldo suficiente) e RN-003 (conta não pode ser paga duas vezes).
 *
 * Conceito pedagógico: o usuário aprende que as contas precisam ser pagas
 * com o saldo disponível. Gastar mais do que se tem não é uma opção na simulação,
 * reforçando o hábito de planejar antes de comprometer a renda.
 */
class PayBillUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val billRepository: BillRepository,
    private val transactionRepository: TransactionRepository
) {

    /**
     * @param account Conta corrente atual do usuário.
     * @param bill    Conta (despesa) a ser paga.
     * @return [UseCaseResult.Success] com a conta atualizada após o débito, ou
     *         [UseCaseResult.Failure] com mensagem educativa.
     */
    suspend operator fun invoke(account: Account, bill: Bill): UseCaseResult<Account> {
        if (FinancialRules.isBillAlreadyPaid(bill)) {
            return UseCaseResult.Failure(
                "Esta conta já foi paga. Ótimo! Continue acompanhando as próximas."
            )
        }
        if (!FinancialRules.canPayBill(account, bill)) {
            return UseCaseResult.Failure(
                "Saldo insuficiente para pagar esta conta. " +
                    "Verifique seu saldo e planeje seus gastos antes de comprometer sua renda."
            )
        }

        val updatedAccount = account.copy(
            balance = account.balance - bill.amount,
            updatedAt = System.currentTimeMillis()
        )
        accountRepository.update(updatedAccount)

        val paidBill = bill.copy(isPaid = true)
        billRepository.update(paidBill)

        transactionRepository.save(
            Transaction(
                accountId = account.id,
                type = TransactionType.BILL_PAYMENT,
                amount = bill.amount,
                description = bill.name,
                month = bill.month
            )
        )

        return UseCaseResult.Success(updatedAccount)
    }
}
