package com.finsim.app.application.usecase

import com.finsim.app.domain.model.Achievement
import com.finsim.app.domain.model.Mission
import com.finsim.app.domain.model.UserMissionProgress
import com.finsim.app.domain.repository.UserAchievementRepository
import com.finsim.app.domain.repository.UserMissionRepository
import com.finsim.app.simulation.missions.AchievementCatalog
import com.finsim.app.simulation.missions.MissionCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class MissionWithProgress(
    val mission: Mission,
    val progress: UserMissionProgress,
)

data class ProgressSummary(
    val missions: List<MissionWithProgress>,
    val unlockedAchievements: List<Achievement>,
    val completedMissionCount: Int,
    val totalMissionCount: Int,
)

class GetProgressUseCase @Inject constructor(
    private val userMissionRepository: UserMissionRepository,
    private val userAchievementRepository: UserAchievementRepository,
) {
    operator fun invoke(profileId: Long): Flow<ProgressSummary> =
        combine(
            userMissionRepository.getByProfileId(profileId),
            userAchievementRepository.getByProfileId(profileId),
        ) { missionProgressList, achievementRecords ->
            val progressMap = missionProgressList.associateBy { it.missionId }

            val missions = MissionCatalog.all.map { mission ->
                val progress = progressMap[mission.id] ?: UserMissionProgress(
                    profileId = profileId,
                    missionId = mission.id,
                    currentProgress = 0L,
                    isCompleted = false,
                    completedMonth = null,
                )
                MissionWithProgress(mission, progress)
            }

            val unlockedAchievements = achievementRecords.mapNotNull { record ->
                AchievementCatalog.getById(record.achievementId)
            }

            ProgressSummary(
                missions = missions,
                unlockedAchievements = unlockedAchievements,
                completedMissionCount = missions.count { it.progress.isCompleted },
                totalMissionCount = missions.size,
            )
        }
}
