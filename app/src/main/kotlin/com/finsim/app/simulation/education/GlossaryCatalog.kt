package com.finsim.app.simulation.education

object GlossaryCatalog {

    val all: List<GlossaryTerm> = listOf(
        GlossaryTerm(
            id = "reserva_emergencia",
            term = "Reserva de Emergência",
            definition = "Dinheiro guardado para cobrir gastos inesperados sem precisar fazer dívidas. " +
                "O ideal é ter entre 3 e 6 meses de despesas mensais guardados.",
            example = "Se você gasta R\$ 1.000 por mês, sua reserva ideal é entre R\$ 3.000 e R\$ 6.000.",
        ),
        GlossaryTerm(
            id = "tesouro_selic",
            term = "Tesouro Selic",
            definition = "Investimento de renda fixa emitido pelo governo federal. " +
                "Rende de acordo com a taxa Selic e tem alta liquidez — você pode resgatar quando quiser.",
            example = "Com R\$ 500 no Tesouro Selic e Selic em 10% ao ano, você ganha cerca de R\$ 50 por ano.",
        ),
        GlossaryTerm(
            id = "cdb",
            term = "CDB (Certificado de Depósito Bancário)",
            definition = "Investimento emitido por bancos. Você empresta dinheiro ao banco e recebe juros em troca. " +
                "Geralmente rende um percentual do CDI.",
            example = "Um CDB que rende 100% do CDI com CDI em 10% ao ano = R\$ 1.000 viram R\$ 1.100 após 1 ano.",
        ),
        GlossaryTerm(
            id = "selic",
            term = "Taxa Selic",
            definition = "A taxa básica de juros da economia brasileira, definida pelo Banco Central. " +
                "Ela influencia todos os outros juros do país: dos investimentos às dívidas.",
            example = "Quando a Selic sobe, investimentos de renda fixa rendem mais, mas crédito fica mais caro.",
        ),
        GlossaryTerm(
            id = "cdi",
            term = "CDI (Certificado de Depósito Interbancário)",
            definition = "Taxa usada entre bancos para empréstimos de curtíssimo prazo. " +
                "Fica muito próxima da Selic e serve de referência para muitos investimentos.",
            example = "Um investimento que rende '110% do CDI' rende um pouco mais do que a Selic.",
        ),
        GlossaryTerm(
            id = "inflacao",
            term = "Inflação",
            definition = "Aumento geral dos preços ao longo do tempo. Com inflação, o mesmo dinheiro compra menos coisas. " +
                "Por isso é importante investir — para que o dinheiro não perca valor.",
            example = "Se a inflação for 5% e você não investir, R\$ 1.000 hoje vão comprar o que R\$ 952 compram daqui a um ano.",
        ),
        GlossaryTerm(
            id = "liquidez",
            term = "Liquidez",
            definition = "Facilidade de converter um investimento em dinheiro sem perda. " +
                "Alta liquidez = você resgata rápido. Baixa liquidez = precisa esperar.",
            example = "O Tesouro Selic tem alta liquidez. Um imóvel tem baixa liquidez — pode demorar meses para vender.",
        ),
        GlossaryTerm(
            id = "risco",
            term = "Risco",
            definition = "Chance de um investimento render menos do que o esperado ou até gerar prejuízo. " +
                "Maior risco normalmente vem acompanhado de maior potencial de ganho.",
            example = "Ações têm risco alto — podem cair muito em crises. Tesouro Selic tem risco baixo.",
        ),
        GlossaryTerm(
            id = "dividendos",
            term = "Dividendos",
            definition = "Parte do lucro de uma empresa distribuída aos seus acionistas. " +
                "Quem tem ações de empresas lucrativas pode receber dividendos periodicamente.",
            example = "Se você tem 10 ações e a empresa paga R\$ 2 por ação, você recebe R\$ 20 de dividendos.",
        ),
        GlossaryTerm(
            id = "patrimonio",
            term = "Patrimônio",
            definition = "Tudo o que você possui de valor: dinheiro em conta, investimentos, bens. " +
                "Patrimônio líquido é o total de bens menos as dívidas.",
            example = "Saldo R\$ 500 + Reserva R\$ 1.500 + Investimentos R\$ 2.000 = Patrimônio de R\$ 4.000.",
        ),
        GlossaryTerm(
            id = "renda_fixa",
            term = "Renda Fixa",
            definition = "Investimento em que as regras de rendimento são conhecidas antes de investir. " +
                "O risco é baixo e o retorno é previsível.",
            example = "Tesouro Selic e CDB são renda fixa. Você já sabe aproximadamente quanto vai receber.",
        ),
        GlossaryTerm(
            id = "renda_variavel",
            term = "Renda Variável",
            definition = "Investimento em que o rendimento oscila e não é garantido. " +
                "O preço pode subir muito ou cair muito dependendo do mercado.",
            example = "Ações são renda variável. Uma ação pode valer R\$ 10 hoje e R\$ 7 amanhã.",
        ),
        GlossaryTerm(
            id = "juros_compostos",
            term = "Juros Compostos",
            definition = "Juros que incidem não só sobre o valor inicial, mas também sobre os juros já acumulados. " +
                "Com o tempo, o efeito é de 'bola de neve' — o crescimento acelera.",
            example = "R\$ 1.000 a 1% ao mês: após 12 meses = R\$ 1.127 (não R\$ 1.120). A diferença vem dos juros sobre juros.",
        ),
        GlossaryTerm(
            id = "diversificacao",
            term = "Diversificação",
            definition = "Estratégia de distribuir os investimentos em diferentes tipos de ativos para reduzir o risco. " +
                "Não colocar todos os ovos em uma cesta só.",
            example = "Ter 50% em renda fixa e 50% em ações é mais seguro do que ter 100% em ações.",
        ),
    )

    fun getById(id: String): GlossaryTerm? = all.find { it.id == id }
}
