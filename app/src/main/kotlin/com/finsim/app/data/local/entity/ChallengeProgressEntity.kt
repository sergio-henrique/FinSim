package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finsim.app.domain.model.ChallengeProgress
import com.finsim.app.domain.model.ChallengeStatus

@Entity(tableName = "challenge_progress")
data class ChallengeProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val challengeId: String,
    val status: String,
    val startMonth: Int,
    val resolvedMonth: Int?,
) {
    fun toDomain() = ChallengeProgress(
        id = id,
        profileId = profileId,
        challengeId = challengeId,
        status = ChallengeStatus.valueOf(status),
        startMonth = startMonth,
        resolvedMonth = resolvedMonth,
    )

    companion object {
        fun fromDomain(d: ChallengeProgress) = ChallengeProgressEntity(
            id = d.id,
            profileId = d.profileId,
            challengeId = d.challengeId,
            status = d.status.name,
            startMonth = d.startMonth,
            resolvedMonth = d.resolvedMonth,
        )
    }
}
