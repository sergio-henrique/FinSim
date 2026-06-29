package com.finsim.app.application.usecase

import com.finsim.app.domain.repository.UserMissionRepository
import com.finsim.app.simulation.missions.MissionEngine
import javax.inject.Inject

class InitializeMissionsUseCase @Inject constructor(
    private val userMissionRepository: UserMissionRepository,
) {
    suspend operator fun invoke(profileId: Long) {
        val initial = MissionEngine.initializeMissions(profileId)
        userMissionRepository.saveAll(initial)
    }
}
