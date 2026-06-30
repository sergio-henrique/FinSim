package com.finsim.app.application.usecase

import com.finsim.app.domain.model.UserProfile
import com.finsim.app.domain.repository.MonthlySnapshotRepository
import com.finsim.app.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

data class RankingEntry(
    val profile: UserProfile,
    val latestHealthScore: Int,
    val totalWealth: Long,
    val monthsSimulated: Int,
    val position: Int,
)

class GetRankingUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val snapshotRepository: MonthlySnapshotRepository,
) {
    fun invoke(): Flow<List<RankingEntry>> = flow {
        userProfileRepository.getAll().collect { profiles ->
            val entries = profiles.map { profile ->
                val latestSnapshot = snapshotRepository.getLatestByProfileId(profile.id)
                RankingEntry(
                    profile = profile,
                    latestHealthScore = latestSnapshot?.financialHealthScore ?: 0,
                    totalWealth = latestSnapshot?.totalWealth ?: 0L,
                    monthsSimulated = maxOf(0, profile.currentMonth - 1),
                    position = 0,
                )
            }

            // Ordena: score desc, depois patrimônio desc
            val sorted = entries
                .sortedWith(compareByDescending<RankingEntry> { it.latestHealthScore }
                    .thenByDescending { it.totalWealth })
                .mapIndexed { index, entry -> entry.copy(position = index + 1) }

            emit(sorted)
        }
    }
}
