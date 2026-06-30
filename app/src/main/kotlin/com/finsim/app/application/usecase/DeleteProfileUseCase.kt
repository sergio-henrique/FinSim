package com.finsim.app.application.usecase

import com.finsim.app.domain.repository.UserProfileRepository
import javax.inject.Inject

/**
 * Exclui um perfil e toda a sua simulação local.
 * Irreversível — a UI deve confirmar antes de chamar.
 */
class DeleteProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
) {
    suspend operator fun invoke(profileId: Long) =
        userProfileRepository.deleteById(profileId)
}
