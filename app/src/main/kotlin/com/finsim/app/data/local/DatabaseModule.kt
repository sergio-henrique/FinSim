package com.finsim.app.data.local

import android.content.Context
import androidx.room.Room
import com.finsim.app.data.local.dao.AccountDao
import com.finsim.app.data.local.dao.BillDao
import com.finsim.app.data.local.dao.FixedIncomeInvestmentDao
import com.finsim.app.data.local.dao.MonthlySnapshotDao
import com.finsim.app.data.local.dao.TransactionDao
import com.finsim.app.data.local.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt que provê o banco Room e todos os DAOs.
 *
 * O banco é singleton para garantir acesso exclusivo ao arquivo SQLite.
 * [fallbackToDestructiveMigration] é usado enquanto não há usuários
 * em produção — deve ser substituído por migrations explícitas antes
 * do lançamento público.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinSimDatabase =
        Room.databaseBuilder(
            context,
            FinSimDatabase::class.java,
            "finsim.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserProfileDao(db: FinSimDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    fun provideAccountDao(db: FinSimDatabase): AccountDao = db.accountDao()

    @Provides
    fun provideTransactionDao(db: FinSimDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideBillDao(db: FinSimDatabase): BillDao = db.billDao()

    @Provides
    fun provideFixedIncomeInvestmentDao(db: FinSimDatabase): FixedIncomeInvestmentDao =
        db.fixedIncomeInvestmentDao()

    @Provides
    fun provideMonthlySnapshotDao(db: FinSimDatabase): MonthlySnapshotDao =
        db.monthlySnapshotDao()
}
