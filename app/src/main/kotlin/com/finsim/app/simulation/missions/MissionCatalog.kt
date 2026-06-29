package com.finsim.app.simulation.missions

import com.finsim.app.domain.model.Mission
import com.finsim.app.domain.model.MissionUnit

/**
 * Catálogo de todas as missões educativas do FinSim.
 *
 * As missões são definições imutáveis — não são persistidas.
 * O progresso do usuário em cada missão é armazenado em [UserMissionProgress].
 *
 * Critério pedagógico: cada missão reforça um hábito financeiro saudável
 * e inclui uma explicação de por que aquele hábito importa.
 */
object MissionCatalog {

    const val FIRST_BILL_PAID = "first_bill_paid"
    const val ALL_BILLS_PAID = "all_bills_paid"
    const val BUILD_RESERVE = "build_reserve"
    const val FIRST_INVESTMENT = "first_investment"
    const val ADVANCE_3_MONTHS = "advance_3_months"
    const val INVEST_500 = "invest_500"
    const val FULL_RESERVE = "full_reserve"

    val all: List<Mission> = listOf(
        Mission(
            id = FIRST_BILL_PAID,
            title = "Primeira conta paga",
            description = "Pague a sua primeira conta mensal.",
            educationalMessage = "Pagar contas em dia evita juros, multas e o acúmulo de dívidas. É o primeiro passo para uma vida financeira organizada.",
            targetValue = 1L,
            unit = MissionUnit.COUNT,
        ),
        Mission(
            id = ALL_BILLS_PAID,
            title = "Mês em dia",
            description = "Pague todas as contas de um mês.",
            educationalMessage = "Quitar todas as despesas mensais antes de investir é o princípio básico do orçamento pessoal: primeiro as obrigações, depois o crescimento.",
            targetValue = 1L,
            unit = MissionUnit.COUNT,
        ),
        Mission(
            id = BUILD_RESERVE,
            title = "Começo de reserva",
            description = "Guarde R$ 300 na reserva de emergência.",
            educationalMessage = "A reserva de emergência é a base de qualquer planejamento financeiro. Com ela, imprevistos não viram dívidas.",
            targetValue = 30_000L,
            unit = MissionUnit.CENTS,
        ),
        Mission(
            id = FIRST_INVESTMENT,
            title = "Primeiro investimento",
            description = "Faça sua primeira aplicação em renda fixa.",
            educationalMessage = "Investir pela primeira vez é um marco. Mesmo pequenas quantias, aplicadas regularmente, crescem com o tempo graças aos juros compostos.",
            targetValue = 1L,
            unit = MissionUnit.COUNT,
        ),
        Mission(
            id = ADVANCE_3_MONTHS,
            title = "Três meses de simulação",
            description = "Avance 3 meses na simulação.",
            educationalMessage = "Consistência é mais importante que perfeição. Manter bons hábitos financeiros por meses seguidos constrói uma base sólida.",
            targetValue = 3L,
            unit = MissionUnit.MONTHS,
        ),
        Mission(
            id = INVEST_500,
            title = "Investidor consistente",
            description = "Alcance R$ 500 investidos em renda fixa.",
            educationalMessage = "R$ 500 investidos a 0,8% ao mês rendem cerca de R$ 50 por ano. É pouco agora, mas o hábito de investir regularmente vale mais do que o valor.",
            targetValue = 50_000L,
            unit = MissionUnit.CENTS,
        ),
        Mission(
            id = FULL_RESERVE,
            title = "Reserva sólida",
            description = "Guarde R$ 1.000 na reserva de emergência.",
            educationalMessage = "R$ 1.000 de reserva cobre a maioria dos imprevistos do dia a dia. O ideal é ter de 3 a 6 meses de despesas guardados.",
            targetValue = 100_000L,
            unit = MissionUnit.CENTS,
        ),
    )

    fun getById(id: String): Mission? = all.firstOrNull { it.id == id }
}
