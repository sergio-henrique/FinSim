package com.finsim.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finsim.app.data.local.dao.AccountDao
import com.finsim.app.data.local.dao.BillDao
import com.finsim.app.data.local.dao.FixedIncomeInvestmentDao
import com.finsim.app.data.local.dao.MonthlySnapshotDao
import com.finsim.app.data.local.dao.TransactionDao
import com.finsim.app.data.local.dao.UserAchievementRecordDao
import com.finsim.app.data.local.dao.UserMissionProgressDao
import com.finsim.app.data.local.dao.UserProfileDao
import com.finsim.app.data.local.entity.AccountEntity
import com.finsim.app.data.local.entity.BillEntity
import com.finsim.app.data.local.entity.FixedIncomeInvestmentEntity
import com.finsim.app.data.local.entity.MonthlySnapshotEntity
import com.finsim.app.data.local.entity.TransactionEntity
import com.finsim.app.data.local.entity.UserAchievementRecordEntity
import com.finsim.app.data.local.entity.UserMissionProgressEntity
import com.finsim.app.data.local.entity.UserProfileEntity

/**
 * Banco de dados Room do FinSim.
 *
 * Versão 2 — Sprint 3: adicionadas tabelas de missões e conquistas.
 * fallbackToDestructiveMigration é aceitável enquanto não houver usuários em produção.
 *
 * Todos os valores monetários usam Long (centavos).
 * Nenhum dado pessoal sensível é armazenado neste banco.
 */
@Database(
    entities = [
        UserProfileEntity::class,
        AccountEntity::class,
        TransactionEntity::class,
        BillEntity::class,
        FixedIncomeInvestmentEntity::class,
        MonthlySnapshotEntity::class,
        UserMissionProgressEntity::class,
        UserAchievementRecordEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class FinSimDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun billDao(): BillDao
    abstract fun fixedIncomeInvestmentDao(): FixedIncomeInvestmentDao
    abstract fun monthlySnapshotDao(): MonthlySnapshotDao
    abstract fun userMissionProgressDao(): UserMissionProgressDao
    abstract fun userAchievementRecordDao(): UserAchievementRecordDao
}
