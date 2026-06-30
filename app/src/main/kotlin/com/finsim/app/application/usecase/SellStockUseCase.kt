package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Transaction
import com.finsim.app.domain.model.TransactionType
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.StockHoldingRepository
import com.finsim.app.domain.repository.StockPriceRepository
import com.finsim.app.domain.repository.TransactionRepository
import com.finsim.app.domain.repository.UserProfileRepository
import com.finsim.app.simulation.variableincome.StockCatalog
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso: Vender ações de um ativo simulado.
 *
 * RN: o usuário só pode vender ações que possui.
 * Ao vender parcialmente, o preço médio das ações restantes é mantido.
 * O lucro/prejuízo é calculado pela diferença entre o preço de venda
 * e o preço médio de compra.
 *
 * Pedagogia: mostra lucro e prejuízo realizados e o conceito de
 * imposto sobre ganho de capital (explicado didaticamente, sem simulação).
 */
class SellStockUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val accountRepository: AccountRepository,
    private val stockPriceRepository: StockPriceRepository,
    private val stockHoldingRepository: StockHoldingRepository,
    private val transactionRepository: TransactionRepository,
) {
    data class SellResult(
        val proceeds: Long,
        val gainLoss: Long,
        val isProfit: Boolean,
    )

    suspend operator fun invoke(
        profileId: Long,
        ticker: String,
        quantity: Int,
    ): UseCaseResult<SellResult> {
        if (quantity <= 0) {
            return UseCaseResult.Failure("Informe uma quantidade válida de ações para vender.")
        }

        val asset = StockCatalog.getByTicker(ticker)
            ?: return UseCaseResult.Failure("Ativo '$ticker' não encontrado no mercado simulado.")

        val holding = stockHoldingRepository.getByProfileAndTicker(profileId, ticker)
            ?: return UseCaseResult.Failure("Você não possui ações de ${asset.name} para vender.")

        if (quantity > holding.quantity) {
            return UseCaseResult.Failure(
                "Você possui apenas ${holding.quantity} ação(ões) de ${asset.name}. " +
                "Não é possível vender $quantity."
            )
        }

        val currentPrice = stockPriceRepository.getByTicker(ticker)?.currentPriceCents
            ?: asset.basePriceCents

        val proceeds = currentPrice * quantity
        val costBasis = holding.averagePriceCents * quantity
        val gainLoss = proceeds - costBasis

        val account = accountRepository.getByProfileId(profileId).first()
            ?: return UseCaseResult.Failure("Conta não encontrada.")

        val profile = userProfileRepository.getById(profileId)
            ?: return UseCaseResult.Failure("Perfil não encontrado.")

        accountRepository.update(account.copy(balance = account.balance + proceeds))

        val newQuantity = holding.quantity - quantity
        if (newQuantity == 0) {
            stockHoldingRepository.delete(holding)
        } else {
            val newTotalInvested = holding.totalInvestedCents - costBasis
            stockHoldingRepository.update(
                holding.copy(
                    quantity = newQuantity,
                    totalInvestedCents = newTotalInvested,
                )
            )
        }

        val gainLossLabel = if (gainLoss >= 0) "lucro" else "prejuízo"
        transactionRepository.save(
            Transaction(
                accountId = account.id,
                type = TransactionType.INCOME,
                amount = proceeds,
                description = "Venda de $quantity ação(ões) $ticker — $gainLossLabel de R$ ${Math.abs(gainLoss) / 100}",
                month = profile.currentMonth,
            )
        )

        return UseCaseResult.Success(
            SellResult(
                proceeds = proceeds,
                gainLoss = gainLoss,
                isProfit = gainLoss >= 0,
            )
        )
    }
}
