package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finsim.app.data.local.entity.BillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BillEntity): Long

    @Update
    suspend fun update(entity: BillEntity)

    @Query(
        """
        SELECT * FROM bills
        WHERE profileId = :profileId AND month = :month
        ORDER BY name ASC
        """
    )
    fun getByProfileIdAndMonth(profileId: Long, month: Int): Flow<List<BillEntity>>

    /**
     * Marca uma conta como paga ou não paga sem substituir toda a linha,
     * reduzindo a chance de sobrescrever mudanças concorrentes.
     */
    @Query("UPDATE bills SET isPaid = :isPaid WHERE id = :billId")
    suspend fun updatePaidStatus(billId: Long, isPaid: Boolean)
}
