package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finsim.app.data.local.entity.StockPriceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockPriceDao {

    @Query("SELECT * FROM stock_prices ORDER BY ticker ASC")
    fun getAll(): Flow<List<StockPriceEntity>>

    @Query("SELECT * FROM stock_prices WHERE ticker = :ticker LIMIT 1")
    suspend fun getByTicker(ticker: String): StockPriceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(price: StockPriceEntity)
}
