package com.finsim.app.data.repository

import com.finsim.app.data.local.dao.AccountDao
import com.finsim.app.data.local.entity.AccountEntity
import com.finsim.app.domain.model.Account
import com.finsim.app.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val dao: AccountDao
) : AccountRepository {

    override suspend fun save(account: Account): Long =
        dao.insert(account.toEntity())

    override suspend fun update(account: Account) =
        dao.update(account.toEntity())

    override fun getByProfileId(profileId: Long): Flow<Account?> =
        dao.getByProfileId(profileId).map { it?.toDomain() }
}

// --- Mappers ---

private fun Account.toEntity() = AccountEntity(
    id = id,
    profileId = profileId,
    balance = balance,
    emergencyReserveBalance = emergencyReserveBalance,
    updatedAt = updatedAt
)

private fun AccountEntity.toDomain() = Account(
    id = id,
    profileId = profileId,
    balance = balance,
    emergencyReserveBalance = emergencyReserveBalance,
    updatedAt = updatedAt
)
