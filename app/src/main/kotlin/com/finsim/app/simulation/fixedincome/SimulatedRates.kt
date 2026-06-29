package com.finsim.app.simulation.fixedincome

import kotlin.math.pow

/**
 * Taxas simuladas para fins educativos.
 *
 * AVISO PEDAGOGICO OBRIGATORIO:
 * Estas taxas são fictícias e servem exclusivamente para demonstrar
 * conceitos de rendimento em uma simulação educativa. Elas NÃO
 * representam valores reais de mercado e NÃO constituem recomendação
 * de investimento de qualquer natureza.
 *
 * As taxas foram escolhidas por serem próximas a valores históricos
 * razoáveis, facilitando a comparação intuitiva do educando, mas
 * podem ser ajustadas a qualquer momento sem afetar a lógica de negócio.
 */
object SimulatedRates {

    /**
     * Taxa mensal simulada do Tesouro Selic: 0,8% ao mês.
     *
     * Conceito pedagógico: o Tesouro Selic acompanha a taxa básica de
     * juros da economia (Selic). Na simulação, usamos um valor fixo para
     * que o educando veja o crescimento sem variações que possam confundir
     * no início do aprendizado.
     */
    const val TESOURO_SELIC_MONTHLY: Double = 0.008

    /**
     * Taxa mensal simulada de CDB: 0,9% ao mês.
     *
     * Conceito pedagógico: o CDB (Certificado de Depósito Bancário) tende
     * a oferecer uma taxa ligeiramente superior ao Tesouro Selic por ter
     * um risco um pouco maior (depende da saúde do banco emissor).
     * Esse contraste ajuda a ensinar a relação risco × retorno.
     *
     * Disponível a partir do MVP 2.
     */
    const val CDB_MONTHLY: Double = 0.009

    /**
     * Converte uma taxa mensal para a taxa anual equivalente (juros compostos).
     *
     * Fórmula: taxa_anual = (1 + taxa_mensal)^12 - 1
     *
     * Conceito pedagógico: a taxa anual NÃO é simplesmente 12 vezes a taxa
     * mensal. Por causa dos juros compostos, ela é ligeiramente maior.
     * Exemplo: 0,8%/mês → taxa anual ≈ 10,03%, não 9,6%.
     * Isso ilustra o efeito de capitalização composta ao longo do tempo.
     *
     * @param monthlyRate Taxa mensal em decimal. Ex: 0.008 para 0,8%.
     * @return Taxa anual equivalente em decimal.
     */
    fun monthlyToAnnual(monthlyRate: Double): Double =
        (1.0 + monthlyRate).pow(12.0) - 1.0
}
