package com.finsim.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finsim.app.domain.model.UserAchievementRecord

@Entity(tableName = "user_achievement_records")
data class UserAchievementRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Long,
    val achievementId: String,
    val unlockedMonth: Int,
    val unlockedAt: Long,
) {
    fun toDomain() = UserAchievementRecord(
        id = id,
        profileId = profileId,
        achievementId = achievementId,
        unlockedMonth = unlockedMonth,
        unlockedAt = unlockedAt,
    )

    companion object {
        fun fromDomain(d: UserAchievementRecord) = UserAchievementRecordEntity(
            id = d.id,
            profileId = d.profileId,
            achievementId = d.achievementId,
            unlockedMonth = d.unlockedMonth,
            unlockedAt = d.unlockedAt,
        )
    }
}
