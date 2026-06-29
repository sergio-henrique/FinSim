package com.finsim.app.domain.model

/**
 * Definição imutável de uma missão educativa.
 * Não é persistida — vive apenas no catálogo em memória.
 */
data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val educationalMessage: String,
    val targetValue: Long,
    val unit: MissionUnit,
)

enum class MissionUnit { CENTS, COUNT, MONTHS }

/**
 * Progresso do usuário em uma missão. Persistida no banco.
 */
data class UserMissionProgress(
    val id: Long = 0,
    val profileId: Long,
    val missionId: String,
    val currentProgress: Long,
    val isCompleted: Boolean,
    val completedMonth: Int?,
)
