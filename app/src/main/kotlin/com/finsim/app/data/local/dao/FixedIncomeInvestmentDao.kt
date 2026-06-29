package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finsim.app.data.local.entity.FixedIncomeInvestmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FixedIncomeInvestmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FixedIncomeInvestmentEntity): Long

    @Update
    suspend fun update(entity: FixedIncomeInvestmentEntity)

    @Query("SELECT * FROM fixed_income_investments WHERE profileId = :profileId ORDER BY createdAt ASC")
    fun getByProfileId(profileId: Long): Flow<List<FixedIncomeInvestmentEntity>>

    @Query("SELECT * FROM fixed_income_investments WHERE id = :id")
    suspend fun getById(id: Long): FixedIncomeInvestmentEntity?
}
