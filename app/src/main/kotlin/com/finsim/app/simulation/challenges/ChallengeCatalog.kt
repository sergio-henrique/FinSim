package com.finsim.app.simulation.challenges

import com.finsim.app.domain.model.Challenge
import com.finsim.app.domain.model.ChallengeCriteriaType

/**
 * Catálogo estático de desafios educativos.
 * Os desafios têm dificuldade progressiva e cobrem conceitos distintos.
 */
object ChallengeCatalog {

    val all: List<Challenge> = listOf(
        Challenge(
            id = "colchao_seguranca",
            title = "Colchão de Segurança",
            description = "Construa uma reserva de emergência de pelo menos R\$ 1.500 em até 6 meses simulados.",
            educationalMessage = "A reserva de emergência é o primeiro passo para uma vida financeira saudável. " +
                "Ela protege você de imprevistos sem precisar recorrer a dívidas.",
            emoji = "🛡️",
            durationMonths = 6,
            criteriaType = ChallengeCriteriaType.MINIMUM_RESERVE,
            criteriaValueCents = 150_000L,
        ),
        Challenge(
            id = "patrimonio_crescimento",
            title = "Patrimônio em Crescimento",
            description = "Alcance um patrimônio total de R\$ 5.000 em até 8 meses simulados.",
            educationalMessage = "Patrimônio total é tudo que você possui: saldo, reserva e investimentos. " +
                "Diversificar e investir regularmente faz o patrimônio crescer ao longo do tempo.",
            emoji = "📈",
            durationMonths = 8,
            criteriaType = ChallengeCriteriaType.MINIMUM_WEALTH,
            criteriaValueCents = 500_000L,
        ),
        Challenge(
            id = "grande_investidor",
            title = "Grande Investidor",
            description = "Acumule um patrimônio total de R\$ 15.000 em até 12 meses simulados.",
            educationalMessage = "Investir com consistência e paciência é o caminho para construir riqueza. " +
                "Juros compostos trabalham a seu favor quanto mais tempo você mantém o dinheiro investido.",
            emoji = "🏆",
            durationMonths = 12,
            criteriaType = ChallengeCriteriaType.MINIMUM_WEALTH,
            criteriaValueCents = 1_500_000L,
        ),
    )

    fun getById(id: String): Challenge? = all.find { it.id == id }
}
