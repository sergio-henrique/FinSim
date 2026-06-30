package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finsim.app.data.local.entity.StockPriceHistoryEntity

@Dao
interface StockPriceHistoryDao {

    @Query("SELECT * FROM stock_price_history WHERE ticker = :ticker ORDER BY month ASC")
    suspend fun getByTicker(ticker: String): List<StockPriceHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<StockPriceHistoryEntity>)
}
