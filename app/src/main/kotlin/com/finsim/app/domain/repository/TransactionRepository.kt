package com.finsim.app.domain.repository

import com.finsim.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso ao ledger de transações virtuais.
 */
interface TransactionRepository {
    suspend fun save(transaction: Transaction): Long
    fun getByAccountIdAndMonth(accountId: Long, month: Int): Flow<List<Transaction>>
    fun getAllByAccountId(accountId: Long): Flow<List<Transaction>>
}
