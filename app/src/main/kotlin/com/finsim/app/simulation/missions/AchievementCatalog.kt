package com.finsim.app.simulation.missions

import com.finsim.app.domain.model.Achievement

/**
 * Catálogo de todas as conquistas (badges) do FinSim.
 *
 * Conquistas são desbloqueadas automaticamente ao cumprir marcos específicos.
 * Cada conquista tem uma mensagem educativa reforçando o hábito financeiro.
 */
object AchievementCatalog {

    const val FIRST_STEPS = "first_steps"
    const val ORGANIZED = "organized"
    const val SAVER = "saver"
    const val INVESTOR = "investor"
    const val RESILIENT = "resilient"
    const val BUILDER = "builder"
    const val MONTH_MASTER = "month_master"

    val all: List<Achievement> = listOf(
        Achievement(
            id = FIRST_STEPS,
            title = "Primeiros passos",
            description = "Completou sua primeira missão no FinSim.",
            educationalMessage = "Começar é sempre o passo mais difícil. Você acaba de entrar no caminho da educação financeira.",
            emoji = "🌱",
        ),
        Achievement(
            id = ORGANIZED,
            title = "Organizado",
            description = "Pagou todas as contas de um mês.",
            educationalMessage = "Quem paga as contas em dia economiza em juros e multas. Organização é a base do sucesso financeiro.",
            emoji = "📋",
        ),
        Achievement(
            id = SAVER,
            title = "Poupador",
            description = "Guardou pelo menos R$ 300 na reserva de emergência.",
            educationalMessage = "A reserva de emergência é o escudo contra imprevistos. Com ela, você não precisa de empréstimo quando algo inesperado acontece.",
            emoji = "🛡️",
        ),
        Achievement(
            id = INVESTOR,
            title = "Investidor iniciante",
            description = "Fez seu primeiro investimento em renda fixa.",
            educationalMessage = "Parabéns! Investir significa fazer o seu dinheiro trabalhar por você. Mesmo um pequeno valor, aplicado por muito tempo, faz diferença.",
            emoji = "💰",
        ),
        Achievement(
            id = RESILIENT,
            title = "Resiliente",
            description = "Enfrentou um imprevisto sem zerar a reserva de emergência.",
            educationalMessage = "Imprevistos acontecem. Quem tem reserva os enfrenta sem dívidas. Você mostrou que planeja para o inesperado.",
            emoji = "💪",
        ),
        Achievement(
            id = BUILDER,
            title = "Construtor de patrimônio",
            description = "Atingiu R$ 5.000 de patrimônio total.",
            educationalMessage = "R$ 5.000 é um marco importante! Patrimônio é construído com consistência, disciplina e tempo. Você está no caminho certo.",
            emoji = "🏗️",
        ),
        Achievement(
            id = MONTH_MASTER,
            title = "Mestre do planejamento",
            description = "Completou 3 meses consecutivos de simulação.",
            educationalMessage = "Consistência é mais valiosa do que perfeição. Três meses praticando bons hábitos já criam uma base financeira real.",
            emoji = "📅",
        ),
    )

    fun getById(id: String): Achievement? = all.firstOrNull { it.id == id }
}
