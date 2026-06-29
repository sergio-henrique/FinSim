package com.finsim.app.domain.repository

import com.finsim.app.domain.model.Account
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso à conta corrente virtual.
 */
interface AccountRepository {
    suspend fun save(account: Account): Long
    suspend fun update(account: Account)
    fun getByProfileId(profileId: Long): Flow<Account?>
}
