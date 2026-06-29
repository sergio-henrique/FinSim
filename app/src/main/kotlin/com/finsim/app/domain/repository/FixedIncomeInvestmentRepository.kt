package com.finsim.app.domain.repository

import com.finsim.app.domain.model.FixedIncomeInvestment
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso aos investimentos simulados de renda fixa.
 */
interface FixedIncomeInvestmentRepository {
    suspend fun save(investment: FixedIncomeInvestment): Long
    suspend fun update(investment: FixedIncomeInvestment)
    fun getByProfileId(profileId: Long): Flow<List<FixedIncomeInvestment>>
    suspend fun getById(id: Long): FixedIncomeInvestment?
}
