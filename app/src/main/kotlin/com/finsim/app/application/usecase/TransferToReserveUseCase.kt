package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.Transaction
import com.finsim.app.domain.model.TransactionType
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.TransactionRepository
import com.finsim.app.domain.rule.FinancialRules
import javax.inject.Inject

/**
 * Caso de uso: Mover dinheiro do saldo livre para a reserva de emergência.
 *
 * Aplica RN-005 (valor positivo e saldo suficiente) e RN-006 (reserva protegida).
 *
 * Conceito pedagógico: a reserva de emergência é o alicerce de qualquer
 * vida financeira saudável. Separar esse dinheiro do saldo livre ajuda o
 * usuário a entender que parte da renda não pode ser usada para gastos do dia a dia.
 *
 * @param currentMonth Mês simulado atual, necessário para registrar a transação.
 */
class TransferToReserveUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) {

    /**
     * @param account      Conta corrente atual do usuário.
     * @param amount       Valor em centavos a transferir para a reserva (deve ser positivo).
     * @param currentMonth Mês simulado atual para registro da transação.
     * @return [UseCaseResult.Success] com a conta atualizada, ou
     *         [UseCaseResult.Failure] com mensagem educativa.
     */
    suspend operator fun invoke(
        account: Account,
        amount: Long,
        currentMonth: Int
    ): UseCaseResult<Account> {
        if (!FinancialRules.canTransferToReserve(account, amount)) {
            return UseCaseResult.Failure(
                "Você não tem esse valor disponível no saldo livre. " +
                    "A reserva de emergência é muito importante, mas transfira apenas o que sobrar após pagar suas contas."
            )
        }

        val updatedAccount = account.copy(
            balance = account.balance - amount,
            emergencyReserveBalance = account.emergencyReserveBalance + amount,
            updatedAt = System.currentTimeMillis()
        )
        accountRepository.update(updatedAccount)

        transactionRepository.save(
            Transaction(
                accountId = account.id,
                type = TransactionType.RESERVE_TRANSFER,
                amount = amount,
                description = "Transferência para reserva de emergência",
                month = currentMonth
            )
        )

        return UseCaseResult.Success(updatedAccount)
    }
}
