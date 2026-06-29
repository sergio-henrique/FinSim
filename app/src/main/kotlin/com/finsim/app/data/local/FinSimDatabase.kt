package com.finsim.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finsim.app.data.local.dao.AccountDao
import com.finsim.app.data.local.dao.BillDao
import com.finsim.app.data.local.dao.FixedIncomeInvestmentDao
import com.finsim.app.data.local.dao.MonthlySnapshotDao
import com.finsim.app.data.local.dao.TransactionDao
import com.finsim.app.data.local.dao.UserProfileDao
import com.finsim.app.data.local.entity.AccountEntity
import com.finsim.app.data.local.entity.BillEntity
import com.finsim.app.data.local.entity.FixedIncomeInvestmentEntity
import com.finsim.app.data.local.entity.MonthlySnapshotEntity
import com.finsim.app.data.local.entity.TransactionEntity
import com.finsim.app.data.local.entity.UserProfileEntity

/**
 * Banco de dados Room do FinSim.
 *
 * Versão 1 — MVP 1. Qualquer alteração de esquema futuro deve ser
 * implementada via Migration explícita; [fallbackToDestructiveMigration]
 * é aceitável apenas enquanto não houver usuários em produção.
 *
 * Todas as entidades usam Long (centavos) para valores monetários.
 * Nenhum dado pessoal sensível é armazenado neste banco.
 */
@Database(
    entities = [
        UserProfileEntity::class,
        AccountEntity::class,
        TransactionEntity::class,
        BillEntity::class,
        FixedIncomeInvestmentEntity::class,
        MonthlySnapshotEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FinSimDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun billDao(): BillDao
    abstract fun fixedIncomeInvestmentDao(): FixedIncomeInvestmentDao
    abstract fun monthlySnapshotDao(): MonthlySnapshotDao
}
