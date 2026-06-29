package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.TransactionDao
import com.finsim.app.data.local.entity.TransactionEntity
import com.finsim.app.domain.model.Transaction
import com.finsim.app.domain.model.TransactionType
import com.finsim.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun save(transaction: Transaction): Long =
        dao.insert(transaction.toEntity())

    override fun getByAccountIdAndMonth(accountId: Long, month: Int): Flow<List<Transaction>> =
        dao.getByAccountIdAndMonth(accountId, month).map { list -> list.map { it.toDomain() } }

    override fun getAllByAccountId(accountId: Long): Flow<List<Transaction>> =
        dao.getAllByAccountId(accountId).map { list -> list.map { it.toDomain() } }
}

// --- Mappers ---

private fun Transaction.toEntity() = TransactionEntity(
    id = id,
    accountId = accountId,
    type = type.name,
    amount = amount,
    description = description,
    month = month,
    createdAt = createdAt
)

private fun TransactionEntity.toDomain() = Transaction(
    id = id,
    accountId = accountId,
    type = TransactionType.valueOf(type),
    amount = amount,
    description = description,
    month = month,
    createdAt = createdAt
)
