package com.finsim.app.domain.model

/**
 * Definição imutável de uma conquista (badge).
 * Não é persistida — vive apenas no catálogo em memória.
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val educationalMessage: String,
    val emoji: String,
)

/**
 * Registro de conquista desbloqueada por um usuário. Persistida no banco.
 */
data class UserAchievementRecord(
    val id: Long = 0,
    val profileId: Long,
    val achievementId: String,
    val unlockedMonth: Int,
    val unlockedAt: Long = System.currentTimeMillis(),
)
