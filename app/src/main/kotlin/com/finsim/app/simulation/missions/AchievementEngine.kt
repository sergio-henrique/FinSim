package com.finsim.app.simulation.missions

import com.finsim.app.domain.model.Achievement
import com.finsim.app.domain.model.UserMissionProgress

/**
 * Motor puro de avaliação de conquistas.
 *
 * Verifica quais conquistas foram desbloqueadas com base no estado atual
 * e nas missões recém-completadas. Não persiste nada.
 */
object AchievementEngine {

    data class AchievementContext(
        val profileId: Long,
        val currentMonth: Int,
        val totalWealthCents: Long,
        val reserveBalanceCents: Long,
        val hasAnyInvestment: Boolean,
        val allBillsPaidThisMonth: Boolean,
        val survivedEventWithReserveIntact: Boolean,
        val newlyCompletedMissions: List<UserMissionProgress>,
    )

    /**
     * Retorna as conquistas recém-desbloqueadas que ainda não estão em [unlockedIds].
     */
    fun evaluate(
        context: AchievementContext,
        unlockedIds: Set<String>,
    ): List<Achievement> {
        val unlocked = mutableListOf<Achievement>()

        fun check(id: String, condition: Boolean) {
            if (condition && id !in unlockedIds) {
                AchievementCatalog.getById(id)?.let { unlocked += it }
            }
        }

        // Desbloqueada ao completar a primeira missão de qualquer tipo
        check(
            AchievementCatalog.FIRST_STEPS,
            context.newlyCompletedMissions.isNotEmpty() ||
                    unlockedIds.size == 0 && context.newlyCompletedMissions.isNotEmpty(),
        )

        // Pagou todas as contas do mês
        check(AchievementCatalog.ORGANIZED, context.allBillsPaidThisMonth)

        // Reserva ≥ R$ 300 (30_000 centavos)
        check(AchievementCatalog.SAVER, context.reserveBalanceCents >= 30_000L)

        // Fez qualquer investimento
        check(AchievementCatalog.INVESTOR, context.hasAnyInvestment)

        // Sobreviveu a um imprevisto com reserva intacta
        check(AchievementCatalog.RESILIENT, context.survivedEventWithReserveIntact)

        // Patrimônio ≥ R$ 5.000 (500_000 centavos)
        check(AchievementCatalog.BUILDER, context.totalWealthCents >= 500_000L)

        // Completou 3 meses
        check(AchievementCatalog.MONTH_MASTER, context.currentMonth >= 3)

        return unlocked
    }
}
