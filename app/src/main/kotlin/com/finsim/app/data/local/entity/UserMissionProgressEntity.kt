package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finsim.app.domain.model.UserMissionProgress

@Entity(tableName = "user_mission_progress")
data class UserMissionProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val missionId: String,
    val currentProgress: Long,
    val isCompleted: Boolean,
    val completedMonth: Int?,
) {
    fun toDomain() = UserMissionProgress(
        id = id,
        profileId = profileId,
        missionId = missionId,
        currentProgress = currentProgress,
        isCompleted = isCompleted,
        completedMonth = completedMonth,
    )

    companion object {
        fun fromDomain(d: UserMissionProgress) = UserMissionProgressEntity(
            id = d.id,
            profileId = d.profileId,
            missionId = d.missionId,
            currentProgress = d.currentProgress,
            isCompleted = d.isCompleted,
            completedMonth = d.completedMonth,
        )
    }
}
