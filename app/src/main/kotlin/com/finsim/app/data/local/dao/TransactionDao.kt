package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finsim.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TransactionEntity): Long

    /**
     * Retorna todas as transações de uma conta em determinado mês simulado.
     * Ordenadas por [createdAt] para preservar ordem cronológica.
     */
    @Query(
        """
        SELECT * FROM transactions
        WHERE accountId = :accountId AND month = :month
        ORDER BY createdAt ASC
        """
    )
    fun getByAccountIdAndMonth(accountId: Long, month: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY createdAt ASC")
    fun getAllByAccountId(accountId: Long): Flow<List<TransactionEntity>>
}
