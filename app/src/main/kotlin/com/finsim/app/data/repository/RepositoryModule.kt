package com.finsim.app.data.repository

import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.BillRepository
import com.finsim.app.domain.repository.FixedIncomeInvestmentRepository
import com.finsim.app.domain.repository.MonthlySnapshotRepository
import com.finsim.app.domain.repository.TransactionRepository
import com.finsim.app.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt que vincula as interfaces de repositório às suas implementações.
 *
 * O uso de [@Binds] é preferível a [@Provides] neste caso porque as
 * implementações são injetadas via construtor (@Inject constructor),
 * o que gera menos código desnecessário no grafo do Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        impl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindBillRepository(
        impl: BillRepositoryImpl
    ): BillRepository

    @Binds
    @Singleton
    abstract fun bindFixedIncomeInvestmentRepository(
        impl: FixedIncomeInvestmentRepositoryImpl
    ): FixedIncomeInvestmentRepository

    @Binds
    @Singleton
    abstract fun bindMonthlySnapshotRepository(
        impl: MonthlySnapshotRepositoryImpl
    ): MonthlySnapshotRepository
}
