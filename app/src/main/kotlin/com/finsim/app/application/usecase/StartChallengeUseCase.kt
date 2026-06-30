package com.finsim.app.application.usecase

import com.finsim.app.domain.model.ChallengeProgress
import com.finsim.app.domain.model.ChallengeStatus
import com.finsim.app.domain.repository.ChallengeProgressRepository
import com.finsim.app.domain.repository.UserProfileRepository
import javax.inject.Inject

class StartChallengeUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val challengeProgressRepository: ChallengeProgressRepository,
) {
    suspend operator fun invoke(profileId: Long, challengeId: String): UseCaseResult<Unit> {
        val profile = userProfileRepository.getById(profileId)
            ?: return UseCaseResult.Failure("Perfil não encontrado")

        val existing = challengeProgressRepository
            .getActiveByProfileId(profileId)
            .find { it.challengeId == challengeId }

        if (existing != null) return UseCaseResult.Failure("Desafio já está em andamento")

        challengeProgressRepository.save(
            ChallengeProgress(
                profileId = profileId,
                challengeId = challengeId,
                status = ChallengeStatus.ACTIVE,
                startMonth = profile.currentMonth,
            )
        )
        return UseCaseResult.Success(Unit)
    }
}
