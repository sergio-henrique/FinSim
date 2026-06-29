package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.Bill
import com.finsim.app.domain.model.FixedIncomeInvestment
import com.finsim.app.domain.model.MonthlySnapshot
import com.finsim.app.domain.model.UserProfile
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.BillRepository
import com.finsim.app.domain.repository.FixedIncomeInvestmentRepository
import com.finsim.app.domain.repository.MonthlySnapshotRepository
import com.finsim.app.domain.repository.UserProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Dados agregados do mês atual, expostos ao Dashboard e à tela de Resumo.
 *
 * [totalWealth] = saldo livre + reserva de emergência + soma dos [currentAmount]
 * dos investimentos ativos. Representa o patrimônio total simulado do usuário.
 */
data class MonthSummary(
    val profile: UserProfile,
    val account: Account,
    val currentMonthBills: List<Bill>,
    val investments: List<FixedIncomeInvestment>,
    val latestSnapshot: MonthlySnapshot?,
    val totalWealth: Long
)

/**
 * Caso de uso: Obter resumo do mês atual para exibição reativa na UI.
 *
 * Retorna um [Flow] para que a camada de apresentação reaja automaticamente
 * a qualquer mudança de estado — pagamento de conta, investimento, etc.
 *
 * Não lança exceções: se o perfil ou conta não existirem, o Flow simplesmente
 * não emite até que os dados sejam criados.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetMonthSummaryUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val accountRepository: AccountRepository,
    private val billRepository: BillRepository,
    private val investmentRepository: FixedIncomeInvestmentRepository,
    private val snapshotRepository: MonthlySnapshotRepository
) {

    /**
     * @param profileId Id do perfil cujo resumo será observado.
     * @return Flow de [MonthSummary] atualizado a cada mudança relevante.
     */
    operator fun invoke(profileId: Long): Flow<MonthSummary> {
        val profileFlow = userProfileRepository.getAll()
            .map { list -> list.firstOrNull { it.id == profileId } }
            .filterNotNull()

        return profileFlow.flatMapLatest { profile ->
            val accountFlow = accountRepository.getByProfileId(profileId).filterNotNull()
            val billsFlow = billRepository.getByProfileIdAndMonth(profileId, profile.currentMonth)
            val investmentsFlow = investmentRepository.getByProfileId(profileId)

            combine(accountFlow, billsFlow, investmentsFlow) { account, bills, investments ->
                val latestSnapshot = snapshotRepository.getByProfileIdAndMonth(
                    profileId = profileId,
                    month = profile.currentMonth
                )

                val fixedIncomeBalance = investments.sumOf { it.currentAmount }
                val totalWealth = account.balance + account.emergencyReserveBalance + fixedIncomeBalance

                MonthSummary(
                    profile = profile,
                    account = account,
                    currentMonthBills = bills,
                    investments = investments,
                    latestSnapshot = latestSnapshot,
                    totalWealth = totalWealth
                )
            }
        }
    }
}
