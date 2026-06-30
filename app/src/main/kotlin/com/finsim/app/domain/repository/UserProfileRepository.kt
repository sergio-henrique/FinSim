package com.finsim.app.domain.repository

import com.finsim.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso ao perfil do usuário.
 * Implementado pela camada data; consumido pela camada application.
 */
interface UserProfileRepository {
    suspend fun save(profile: UserProfile): Long
    suspend fun update(profile: UserProfile)
    suspend fun getById(id: Long): UserProfile?
    fun getAll(): Flow<List<UserProfile>>
    suspend fun deleteById(id: Long)
}
