package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Challenge
import com.finsim.app.domain.model.ChallengeProgress
import com.finsim.app.domain.model.ChallengeStatus
import com.finsim.app.domain.repository.ChallengeProgressRepository
import com.finsim.app.simulation.challenges.ChallengeCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class ChallengeWithProgress(
    val challenge: Challenge,
    val progress: ChallengeProgress?,
    val status: ChallengeStatus,
)

class GetChallengesUseCase @Inject constructor(
    private val challengeProgressRepository: ChallengeProgressRepository,
) {
    fun invoke(profileId: Long): Flow<List<ChallengeWithProgress>> =
        challengeProgressRepository.getByProfileId(profileId).map { progressList ->
            ChallengeCatalog.all.map { challenge ->
                val progress = progressList.find { it.challengeId == challenge.id }
                ChallengeWithProgress(
                    challenge = challenge,
                    progress = progress,
                    status = progress?.status ?: ChallengeStatus.NOT_STARTED,
                )
            }
        }
}
