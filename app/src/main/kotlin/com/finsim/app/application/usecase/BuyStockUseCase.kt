package com.finsim.app.application.usecase

import com.finsim.app.domain.model.StockHolding
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
 * Caso de uso: Comprar ações de um ativo simulado.
 *
 * RN: o usuário só pode comprar se tiver saldo suficiente.
 * O preço médio é recalculado com cada nova compra (preço médio ponderado).
 *
 * Pedagogia: mostra como calcular o preço médio e por que comprar em
 * queda pode reduzir o preço médio da posição.
 */
class BuyStockUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val accountRepository: AccountRepository,
    private val stockPriceRepository: StockPriceRepository,
    private val stockHoldingRepository: StockHoldingRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(
        profileId: Long,
        ticker: String,
        quantity: Int,
    ): UseCaseResult<StockHolding> {
        if (quantity <= 0) {
            return UseCaseResult.Failure("Informe uma quantidade válida de ações para comprar.")
        }

        val asset = StockCatalog.getByTicker(ticker)
            ?: return UseCaseResult.Failure("Ativo '$ticker' não encontrado no mercado simulado.")

        val currentPrice = stockPriceRepository.getByTicker(ticker)?.currentPriceCents
            ?: asset.basePriceCents

        val totalCost = currentPrice * quantity

        val account = accountRepository.getByProfileId(profileId).first()
            ?: return UseCaseResult.Failure("Conta não encontrada.")

        if (account.balance < totalCost) {
            return UseCaseResult.Failure(
                "Saldo insuficiente para comprar $quantity ação(ões) de ${asset.name}. " +
                "Custo: R$ ${totalCost / 100}. Saldo disponível: R$ ${account.balance / 100}."
            )
        }

        val profile = userProfileRepository.getById(profileId)
            ?: return UseCaseResult.Failure("Perfil não encontrado.")

        // Atualiza saldo
        accountRepository.update(account.copy(balance = account.balance - totalCost))

        // Calcula ou atualiza posição (preço médio ponderado)
        val existing = stockHoldingRepository.getByProfileAndTicker(profileId, ticker)
        val updatedHolding = if (existing != null) {
            val newQuantity = existing.quantity + quantity
            val newTotalInvested = existing.totalInvestedCents + totalCost
            val newAvgPrice = newTotalInvested / newQuantity
            val updated = existing.copy(
                quantity = newQuantity,
                averagePriceCents = newAvgPrice,
                totalInvestedCents = newTotalInvested,
            )
            stockHoldingRepository.update(updated)
            updated
        } else {
            val newHolding = StockHolding(
                profileId = profileId,
                ticker = ticker,
                quantity = quantity,
                averagePriceCents = currentPrice,
                totalInvestedCents = totalCost,
            )
            stockHoldingRepository.save(newHolding)
            newHolding
        }

        transactionRepository.save(
            Transaction(
                accountId = account.id,
                type = TransactionType.INVESTMENT_APPLICATION,
                amount = totalCost,
                description = "Compra de $quantity ação(ões) $ticker a R$ ${currentPrice / 100}",
                month = profile.currentMonth,
            )
        )

        return UseCaseResult.Success(updatedHolding)
    }
}
