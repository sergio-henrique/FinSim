package com.finsim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.finsim.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UserProfileEntity): Long

    @Update
    suspend fun update(entity: UserProfileEntity)

    @Query("SELECT * FROM user_profiles WHERE id = :id")
    suspend fun getById(id: Long): UserProfileEntity?

    @Query("SELECT * FROM user_profiles ORDER BY createdAt ASC")
    fun getAll(): Flow<List<UserProfileEntity>>
}
