package com.finsim.app.simulation.events

import com.finsim.app.domain.model.RandomEvent
import com.finsim.app.domain.model.RandomEventCategory
import kotlin.random.Random

/**
 * Motor de eventos financeiros aleatórios.
 *
 * Conceito pedagógico:
 * A vida financeira real não é previsível. Imprevistos como um conserto de
 * carro, uma consulta médica ou um eletrodoméstico quebrado acontecem a
 * qualquer momento. A reserva de emergência existe para absorver esses
 * choques sem comprometer o orçamento ou forçar resgates de investimentos.
 *
 * Probabilidade de evento por mês: ~25% (1 em cada 4 meses, em média).
 * O valor é proporcional ao mês simulado para crescer com o educando.
 *
 * Este motor é puro — sem acesso a banco e sem efeitos colaterais.
 * Aceita [Random] como parâmetro para permitir testes determinísticos.
 */
object RandomEventEngine {

    private const val EVENT_PROBABILITY = 0.25

    private val EVENT_TEMPLATES = listOf(
        EventTemplate(
            title = "Consulta médica inesperada",
            description = "Você precisou ir ao médico este mês.",
            baseCents = 15_000L,
            category = RandomEventCategory.HEALTH,
            educational = "Imprevistos de saúde são os mais comuns. Uma reserva de emergência garante que você cuide da saúde sem comprometer outras metas.",
        ),
        EventTemplate(
            title = "Conserto do celular",
            description = "Sua tela rachou e precisou de reparo.",
            baseCents = 20_000L,
            category = RandomEventCategory.APPLIANCE,
            educational = "Eletrônicos quebram. Ter uma reserva evita que você precise parcelar um conserto com juros altos.",
        ),
        EventTemplate(
            title = "Vazamento em casa",
            description = "Um cano furou e precisou de encanador.",
            baseCents = 25_000L,
            category = RandomEventCategory.HOME,
            educational = "Manutenção de casa é inevitável. Quem tem reserva paga à vista; quem não tem, paga juros no crédito.",
        ),
        EventTemplate(
            title = "Pneu furado",
            description = "Um pneu do carro (ou bike) precisou de troca.",
            baseCents = 18_000L,
            category = RandomEventCategory.TRANSPORT,
            educational = "Transporte falha na hora errada. A reserva de emergência é o seu seguro pessoal contra imprevistos.",
        ),
        EventTemplate(
            title = "Veterinário do pet",
            description = "Seu bichinho precisou de uma consulta.",
            baseCents = 12_000L,
            category = RandomEventCategory.PET,
            educational = "Ter um animal de estimação envolve custos imprevisíveis. Incluir isso no planejamento financeiro é ser responsável.",
        ),
        EventTemplate(
            title = "Geladeira com defeito",
            description = "A geladeira deu problema e precisou de técnico.",
            baseCents = 30_000L,
            category = RandomEventCategory.APPLIANCE,
            educational = "Eletrodomésticos têm vida útil limitada. Planejar a substituição futura faz parte de um bom orçamento.",
        ),
    )

    /**
     * Gera um evento aleatório para o mês, ou null se não ocorrer evento.
     *
     * @param month   Mês simulado atual (usado para escalar o valor do evento).
     * @param random  Instância de [Random] — injete [Random.Default] em produção;
     *                use [Random(seed)] nos testes para resultados determinísticos.
     * @return [RandomEvent] ou null.
     */
    fun generate(month: Int, random: Random = Random): RandomEvent? {
        if (random.nextDouble() > EVENT_PROBABILITY) return null

        val template = EVENT_TEMPLATES[random.nextInt(EVENT_TEMPLATES.size)]

        // Escala o valor suavemente conforme os meses avançam (max 2×)
        val scaleFactor = 1.0 + (month - 1) * 0.05
        val amountCents = (template.baseCents * scaleFactor.coerceAtMost(2.0)).toLong()

        return RandomEvent(
            title = template.title,
            description = template.description,
            amountCents = amountCents,
            educationalMessage = template.educational,
            category = template.category,
        )
    }

    private data class EventTemplate(
        val title: String,
        val description: String,
        val baseCents: Long,
        val category: RandomEventCategory,
        val educational: String,
    )
}
