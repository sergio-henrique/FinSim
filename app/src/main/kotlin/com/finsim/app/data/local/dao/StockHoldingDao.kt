package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finsim.app.data.local.entity.StockHoldingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockHoldingDao {

    @Query("SELECT * FROM stock_holdings WHERE profileId = :profileId ORDER BY ticker ASC")
    fun getByProfileId(profileId: Long): Flow<List<StockHoldingEntity>>

    @Query("SELECT * FROM stock_holdings WHERE profileId = :profileId AND ticker = :ticker LIMIT 1")
    suspend fun getByProfileAndTicker(profileId: Long, ticker: String): StockHoldingEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(holding: StockHoldingEntity): Long

    @Update
    suspend fun update(holding: StockHoldingEntity)

    @Delete
    suspend fun delete(holding: StockHoldingEntity)
}
