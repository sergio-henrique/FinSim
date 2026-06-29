package com.finsim.app.simulation.missions

import com.finsim.app.domain.model.MissionUnit
import com.finsim.app.domain.model.UserMissionProgress

/**
 * Motor puro de avaliação de missões.
 *
 * Recebe o estado atual da simulação e retorna a lista de progressos atualizada.
 * Não tem efeitos colaterais — não acessa banco nem repositórios.
 */
object MissionEngine {

    data class SimulationState(
        val profileId: Long,
        val currentMonth: Int,
        val paidBillsCount: Int,
        val totalBillsCount: Int,
        val reserveBalanceCents: Long,
        val fixedIncomeBalanceCents: Long,
        val hasAnyInvestment: Boolean,
    )

    /**
     * Inicializa os progressos de missão para um perfil recém-criado.
     */
    fun initializeMissions(profileId: Long): List<UserMissionProgress> =
        MissionCatalog.all.map { mission ->
            UserMissionProgress(
                profileId = profileId,
                missionId = mission.id,
                currentProgress = 0L,
                isCompleted = false,
                completedMonth = null,
            )
        }

    /**
     * Avalia o estado atual e retorna somente os progressos que mudaram
     * (novos ou atualizados). Missões já completas não são reavaliadas.
     */
    fun evaluate(
        existingProgress: List<UserMissionProgress>,
        state: SimulationState,
    ): List<UserMissionProgress> {
        val progressMap = existingProgress.associateBy { it.missionId }.toMutableMap()

        fun getOrCreate(missionId: String): UserMissionProgress =
            progressMap[missionId] ?: UserMissionProgress(
                profileId = state.profileId,
                missionId = missionId,
                currentProgress = 0L,
                isCompleted = false,
                completedMonth = null,
            )

        val updated = mutableListOf<UserMissionProgress>()

        MissionCatalog.all.forEach { mission ->
            val current = getOrCreate(mission.id)
            if (current.isCompleted) return@forEach

            val newProgress = computeProgress(mission.id, state, current.currentProgress)
            val isNowComplete = newProgress >= mission.targetValue

            if (newProgress != current.currentProgress || isNowComplete != current.isCompleted) {
                updated += current.copy(
                    currentProgress = newProgress,
                    isCompleted = isNowComplete,
                    completedMonth = if (isNowComplete) state.currentMonth else null,
                )
            }
        }

        return updated
    }

    private fun computeProgress(
        missionId: String,
        state: SimulationState,
        previousProgress: Long,
    ): Long = when (missionId) {
        MissionCatalog.FIRST_BILL_PAID ->
            if (state.paidBillsCount > 0) 1L else previousProgress

        MissionCatalog.ALL_BILLS_PAID ->
            if (state.totalBillsCount > 0 && state.paidBillsCount >= state.totalBillsCount) 1L
            else previousProgress

        MissionCatalog.BUILD_RESERVE ->
            maxOf(previousProgress, state.reserveBalanceCents)

        MissionCatalog.FIRST_INVESTMENT ->
            if (state.hasAnyInvestment) 1L else previousProgress

        MissionCatalog.ADVANCE_3_MONTHS ->
            maxOf(previousProgress, state.currentMonth.toLong())

        MissionCatalog.INVEST_500 ->
            maxOf(previousProgress, state.fixedIncomeBalanceCents)

        MissionCatalog.FULL_RESERVE ->
            maxOf(previousProgress, state.reserveBalanceCents)

        else -> previousProgress
    }

    /** Retorna somente as missões recém-concluídas (completedMonth == currentMonth). */
    fun newlyCompleted(
        updates: List<UserMissionProgress>,
        currentMonth: Int,
    ): List<UserMissionProgress> =
        updates.filter { it.isCompleted && it.completedMonth == currentMonth }
}
