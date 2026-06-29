package com.finsim.app.application.usecase

import com.finsim.app.domain.model.UserProfile
import com.finsim.app.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso: Verificar se já existe um perfil salvo localmente.
 *
 * Usado no startup para decidir se o usuário deve ver o onboarding
 * ou ser redirecionado diretamente ao Dashboard.
 *
 * Retorna Flow para reagir a mudanças (ex: perfil criado nesta sessão).
 */
class GetExistingProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
) {
    operator fun invoke(): Flow<UserProfile?> =
        userProfileRepository.getAll().map { it.firstOrNull() }
}
