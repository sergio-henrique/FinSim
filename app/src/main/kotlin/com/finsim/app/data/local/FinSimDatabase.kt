package com.finsim.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finsim.app.data.local.dao.AccountDao
import com.finsim.app.data.local.dao.BillDao
import com.finsim.app.data.local.dao.FixedIncomeInvestmentDao
import com.finsim.app.data.local.dao.MonthlySnapshotDao
import com.finsim.app.data.local.dao.StockHoldingDao
import com.finsim.app.data.local.dao.StockPriceDao
import com.finsim.app.data.local.dao.StockPriceHistoryDao
import com.finsim.app.data.local.dao.TransactionDao
import com.finsim.app.data.local.dao.UserAchievementRecordDao
import com.finsim.app.data.local.dao.UserMissionProgressDao
import com.finsim.app.data.local.dao.UserProfileDao
import com.finsim.app.data.local.entity.AccountEntity
import com.finsim.app.data.local.entity.BillEntity
import com.finsim.app.data.local.entity.FixedIncomeInvestmentEntity
import com.finsim.app.data.local.entity.MonthlySnapshotEntity
import com.finsim.app.data.local.entity.StockHoldingEntity
import com.finsim.app.data.local.entity.StockPriceEntity
import com.finsim.app.data.local.entity.StockPriceHistoryEntity
import com.finsim.app.data.local.entity.TransactionEntity
import com.finsim.app.data.local.entity.UserAchievementRecordEntity
import com.finsim.app.data.local.entity.UserMissionProgressEntity
import com.finsim.app.data.local.entity.UserProfileEntity

/**
 * Banco de dados Room do FinSim.
 *
 * Versão 4 — Sprint 5: adicionada tabela de histórico de preços de ações.
 * fallbackToDestructiveMigration é aceitável enquanto não houver usuários em produção.
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
        StockPriceEntity::class,
        StockHoldingEntity::class,
        StockPriceHistoryEntity::class,
    ],
    version = 4,
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
    abstract fun stockPriceDao(): StockPriceDao
    abstract fun stockHoldingDao(): StockHoldingDao
    abstract fun stockPriceHistoryDao(): StockPriceHistoryDao
}
