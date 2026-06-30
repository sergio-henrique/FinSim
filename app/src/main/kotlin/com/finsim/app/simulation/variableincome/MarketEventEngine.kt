package com.finsim.app.simulation.variableincome

import com.finsim.app.domain.model.MarketEvent
import com.finsim.app.domain.model.MarketEventType
import com.finsim.app.domain.model.StockSector
import kotlin.random.Random

/**
 * Motor de eventos de mercado.
 *
 * Gera eventos que afetam preços de ativos por setor ou de todo o mercado.
 * Probabilidade: 20% de evento por mês. Crashes são raros (3%).
 *
 * Pedagogia: mostra que eventos externos impactam investimentos e que
 * diversificar entre setores reduz o risco concentrado.
 */
object MarketEventEngine {

    private const val EVENT_PROBABILITY = 0.20
    private const val CRASH_PROBABILITY = 0.03

    private data class EventTemplate(
        val title: String,
        val description: String,
        val educationalMessage: String,
        val sector: StockSector?,
        val impactFactor: Double,
        val type: MarketEventType,
    )

    private val events = listOf(
        EventTemplate(
            title = "Alta do petróleo beneficia energia",
            description = "O preço do petróleo subiu no mercado global, favorecendo empresas do setor energético.",
            educationalMessage = "Setores como energia estão ligados a commodities globais. Uma alta no petróleo pode valorizar ações do setor.",
            sector = StockSector.ENERGY,
            impactFactor = 1.12,
            type = MarketEventType.BOOM,
        ),
        EventTemplate(
            title = "Queda na energia por chuvas",
            description = "O excesso de chuvas reduziu o custo de energia elétrica, comprimindo as margens das elétricas.",
            educationalMessage = "Fatores climáticos afetam setores específicos. Diversificar entre setores protege sua carteira.",
            sector = StockSector.ENERGY,
            impactFactor = 0.90,
            type = MarketEventType.BUST,
        ),
        EventTemplate(
            title = "Alta dos juros favorece bancos",
            description = "O banco central elevou os juros, aumentando a margem financeira dos bancos.",
            educationalMessage = "Bancos geralmente lucram mais quando os juros sobem, pois cobram mais nos empréstimos.",
            sector = StockSector.FINANCE,
            impactFactor = 1.08,
            type = MarketEventType.BOOM,
        ),
        EventTemplate(
            title = "Inadimplência pressiona bancos",
            description = "O aumento da inadimplência reduziu os lucros dos bancos neste trimestre.",
            educationalMessage = "Quando as pessoas não pagam suas dívidas, os bancos sofrem. Inadimplência afeta o setor financeiro.",
            sector = StockSector.FINANCE,
            impactFactor = 0.91,
            type = MarketEventType.BUST,
        ),
        EventTemplate(
            title = "Boom de tecnologia",
            description = "Novas regulamentações favoráveis impulsionaram as empresas de tecnologia.",
            educationalMessage = "Tecnologia é um setor volátil que pode subir muito rapidamente com notícias positivas.",
            sector = StockSector.TECHNOLOGY,
            impactFactor = 1.20,
            type = MarketEventType.BOOM,
        ),
        EventTemplate(
            title = "Escândalo abala setor de tech",
            description = "Problemas regulatórios atingiram empresas de tecnologia, causando queda nas ações.",
            educationalMessage = "Riscos regulatórios são reais, especialmente em tecnologia. Diversifique sua carteira para reduzir exposição.",
            sector = StockSector.TECHNOLOGY,
            impactFactor = 0.82,
            type = MarketEventType.BUST,
        ),
        EventTemplate(
            title = "Consumo em alta",
            description = "O aumento da renda das famílias impulsionou as vendas do setor de alimentos.",
            educationalMessage = "Setores de consumo básico tendem a ser estáveis — as pessoas continuam comendo mesmo em crises.",
            sector = StockSector.CONSUMER_GOODS,
            impactFactor = 1.07,
            type = MarketEventType.BOOM,
        ),
        EventTemplate(
            title = "Minério valoriza no exterior",
            description = "A demanda global por minério de ferro aumentou, beneficiando mineradoras brasileiras.",
            educationalMessage = "Exportadoras de commodities dependem da demanda internacional. Isso conecta o Brasil à economia global.",
            sector = StockSector.MINING,
            impactFactor = 1.15,
            type = MarketEventType.BOOM,
        ),
        EventTemplate(
            title = "Queda global no preço do minério",
            description = "O excesso de oferta global derrubou os preços do minério de ferro.",
            educationalMessage = "Commodities como minério são cíclicas — sobem e descem com a demanda global.",
            sector = StockSector.MINING,
            impactFactor = 0.86,
            type = MarketEventType.BUST,
        ),
    )

    private val crashTemplate = EventTemplate(
        title = "Crash de mercado!",
        description = "Uma crise de confiança derrubou a bolsa de valores. Todos os setores foram afetados.",
        educationalMessage = "Crashes de mercado acontecem e são parte do investimento em renda variável. Quem mantém a calma e não vende no pânico geralmente se recupera com o tempo.",
        sector = null,
        impactFactor = 0.70,
        type = MarketEventType.CRASH,
    )

    private val recoveryTemplate = EventTemplate(
        title = "Mercado em recuperação",
        description = "Após o período de queda, investidores voltam a comprar ações.",
        educationalMessage = "Após um crash, o mercado geralmente se recupera. Quem compra mais ações durante a queda pode lucrar mais na recuperação.",
        sector = null,
        impactFactor = 1.10,
        type = MarketEventType.RECOVERY,
    )

    fun generate(
        month: Int,
        wasLastMonthCrash: Boolean = false,
        random: Random = Random,
    ): MarketEvent? {
        if (wasLastMonthCrash) {
            return recoveryTemplate.toEvent()
        }

        if (random.nextDouble() < CRASH_PROBABILITY) {
            return crashTemplate.toEvent()
        }

        if (random.nextDouble() < EVENT_PROBABILITY) {
            return events[random.nextInt(events.size)].toEvent()
        }

        return null
    }

    private fun EventTemplate.toEvent() = MarketEvent(
        title = title,
        description = description,
        educationalMessage = educationalMessage,
        affectedSector = sector,
        priceImpactFactor = impactFactor,
        type = type,
    )
}
