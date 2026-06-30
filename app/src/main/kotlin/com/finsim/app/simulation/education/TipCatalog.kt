package com.finsim.app.simulation.education

object TipCatalog {

    val all: List<FinancialTip> = listOf(
        FinancialTip(
            id = "reserva_6x",
            emoji = "🛡️",
            title = "A regra dos 6 meses",
            body = "Tente guardar o equivalente a 6 meses de gastos na sua reserva de emergência. " +
                "Assim você tem segurança para enfrentar imprevistos sem se endividar.",
        ),
        FinancialTip(
            id = "pay_yourself_first",
            emoji = "💰",
            title = "Pague-se primeiro",
            body = "Ao receber seu salário, separe uma parte para investir antes de gastar. " +
                "Mesmo 10% por mês faz uma diferença enorme ao longo do tempo.",
        ),
        FinancialTip(
            id = "diversificacao",
            emoji = "🧺",
            title = "Não coloque tudo em uma cesta",
            body = "Diversificar significa investir em diferentes tipos de ativos. " +
                "Se um vai mal, os outros podem compensar. É a forma mais inteligente de reduzir riscos.",
        ),
        FinancialTip(
            id = "juros_compostos",
            emoji = "📈",
            title = "O tempo é seu melhor amigo",
            body = "Com juros compostos, quanto mais cedo você começa a investir, maior o crescimento. " +
                "R\$ 100 investidos hoje valem muito mais do que R\$ 100 investidos daqui a 10 anos.",
        ),
        FinancialTip(
            id = "inflacao",
            emoji = "🔥",
            title = "A inflação corrói seu dinheiro",
            body = "Dinheiro parado na conta corrente perde poder de compra com o tempo. " +
                "Investir em renda fixa já protege contra a inflação.",
        ),
        FinancialTip(
            id = "risco_retorno",
            emoji = "⚖️",
            title = "Risco e retorno andam juntos",
            body = "Investimentos com maior potencial de ganho geralmente têm maior risco de perda. " +
                "Conheça seu perfil e nunca invista mais do que pode perder na renda variável.",
        ),
        FinancialTip(
            id = "gastos_conscientes",
            emoji = "🧠",
            title = "Gaste com consciência",
            body = "Antes de comprar algo, pergunte: 'Eu realmente preciso disso?' " +
                "Pequenos gastos diários somam quantias enormes ao longo do mês.",
        ),
        FinancialTip(
            id = "contas_em_dia",
            emoji = "📅",
            title = "Pague as contas em dia",
            body = "Atrasar pagamentos gera juros e prejudica sua saúde financeira. " +
                "Organize suas despesas e priorize sempre as contas essenciais.",
        ),
        FinancialTip(
            id = "meta_financeira",
            emoji = "🎯",
            title = "Defina metas claras",
            body = "Ter um objetivo concreto — como poupar R\$ 1.000 em 6 meses — ajuda a manter o foco. " +
                "Divida metas grandes em pequenas etapas mensais.",
        ),
        FinancialTip(
            id = "longo_prazo",
            emoji = "🌱",
            title = "Pense no longo prazo",
            body = "Investir por anos, não semanas. O mercado sobe e desce no curto prazo, " +
                "mas historicamente cresce no longo prazo. Paciência é uma virtude financeira.",
        ),
    )

    fun forMonth(currentMonth: Int): FinancialTip {
        val index = ((currentMonth - 1) % all.size).coerceAtLeast(0)
        return all[index]
    }
}
