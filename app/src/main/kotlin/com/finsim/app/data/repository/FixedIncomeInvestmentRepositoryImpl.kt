package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.FixedIncomeInvestmentDao
import com.finsim.app.data.local.entity.FixedIncomeInvestmentEntity
import com.finsim.app.domain.model.FixedIncomeInvestment
import com.finsim.app.domain.model.FixedIncomeProductType
import com.finsim.app.domain.model.LiquidityType
import com.finsim.app.domain.repository.FixedIncomeInvestmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FixedIncomeInvestmentRepositoryImpl @Inject constructor(
    private val dao: FixedIncomeInvestmentDao
) : FixedIncomeInvestmentRepository {

    override suspend fun save(investment: FixedIncomeInvestment): Long =
        dao.insert(investment.toEntity())

    override suspend fun update(investment: FixedIncomeInvestment) =
        dao.update(investment.toEntity())

    override fun getByProfileId(profileId: Long): Flow<List<FixedIncomeInvestment>> =
        dao.getByProfileId(profileId).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): FixedIncomeInvestment? =
        dao.getById(id)?.toDomain()
}

// --- Mappers ---

private fun FixedIncomeInvestment.toEntity() = FixedIncomeInvestmentEntity(
    id = id,
    profileId = profileId,
    productType = productType.name,
    investedAmount = investedAmount,
    currentAmount = currentAmount,
    monthlyRateBps = monthlyRateBps,
    startMonth = startMonth,
    maturityMonth = maturityMonth,
    liquidityType = liquidityType.name,
    createdAt = createdAt
)

private fun FixedIncomeInvestmentEntity.toDomain() = FixedIncomeInvestment(
    id = id,
    profileId = profileId,
    productType = FixedIncomeProductType.valueOf(productType),
    investedAmount = investedAmount,
    currentAmount = currentAmount,
    monthlyRateBps = monthlyRateBps,
    startMonth = startMonth,
    maturityMonth = maturityMonth,
    liquidityType = LiquidityType.valueOf(liquidityType),
    createdAt = createdAt
)
