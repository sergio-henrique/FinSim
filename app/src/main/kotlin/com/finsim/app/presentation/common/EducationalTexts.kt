package com.finsim.app.presentation.common

object EducationalTexts {

    // --- Onboarding ---
    object Onboarding {
        const val TITLE = "Bem-vindo ao FinSim!"
        const val SUBTITLE = "Aprenda a cuidar do seu dinheiro de forma segura e divertida."
        const val SIMULATION_DISCLAIMER = "Este é um jogo educativo. Todos os valores são virtuais e não representam dinheiro real."
        const val INCOME_HINT = "Escolha uma renda mensal fictícia para começar sua simulação."
        const val START_BUTTON = "Começar simulação"
    }

    // --- Dashboard ---
    object Dashboard {
        const val BALANCE_LABEL = "Saldo em conta"
        const val RESERVE_LABEL = "Reserva de emergência"
        const val INVESTMENTS_LABEL = "Investimentos"
        const val TOTAL_WEALTH_LABEL = "Patrimônio total"
        const val ADVANCE_MONTH_BUTTON = "Avançar mês"
        const val PENDING_BILLS_LABEL = "Contas pendentes"
        const val NO_PENDING_BILLS = "Nenhuma conta pendente. Ótimo trabalho!"
    }

    // --- Contas ---
    object Bills {
        const val SCREEN_TITLE = "Contas do mês"
        const val PAID_LABEL = "Paga"
        const val PENDING_LABEL = "Pendente"
        const val PAY_BUTTON = "Pagar"
        const val BILL_PAID_FEEDBACK = "Conta paga! Manter as contas em dia protege seu crédito e sua tranquilidade."
        const val ALREADY_PAID = "Esta conta já foi paga neste mês."
        const val INSUFFICIENT_BALANCE_BILLS = "Saldo insuficiente. Tente reduzir outros gastos ou aguardar a próxima renda."
        const val ALL_PAID = "Todas as contas do mês foram pagas. Excelente disciplina financeira!"
        const val TIP_PAY_FIRST = "Dica: pague suas contas antes de investir. Compromissos financeiros têm prioridade."
    }

    // --- Reserva ---
    object Reserve {
        const val SCREEN_TITLE = "Reserva de emergência"
        const val CURRENT_RESERVE_LABEL = "Reserva atual"
        const val GOAL_LABEL = "Meta sugerida"
        const val GOAL_DESCRIPTION = "Ideal: 3 a 6 meses de despesas mensais"
        const val TRANSFER_BUTTON = "Transferir para reserva"
        const val WHAT_IS_RESERVE = "A reserva de emergência é dinheiro guardado para situações inesperadas, como perda de renda ou gastos médicos. Ela dá segurança para você tomar decisões sem desespero."
        const val RESERVE_TRANSFERRED_FEEDBACK = "Transferência realizada! Cada valor guardado aqui é um passo rumo à segurança financeira."
        const val INSUFFICIENT_FOR_RESERVE = "Você não tem esse valor disponível agora. Guarde o que puder — qualquer valor já é um começo."
        const val ZERO_AMOUNT = "Informe um valor maior que zero para transferir."
        const val RESERVE_COMPLETE = "Sua reserva está no nível recomendado! Agora você pode investir com mais tranquilidade."
    }

    // --- Renda Fixa ---
    object FixedIncome {
        const val SCREEN_TITLE = "Renda fixa"
        const val PRODUCT_NAME = "Tesouro Selic Simulado"
        const val RATE_INFO = "Taxa simulada: 0,8% ao mês"
        const val LIQUIDITY_INFO = "Liquidez diária — você pode resgatar a qualquer mês"
        const val APPLY_BUTTON = "Aplicar"
        const val SIMULATION_WARNING = "⚠️ Simulação educativa. Não representa investimento real e não constitui recomendação financeira."
        const val WHAT_IS_FIXED_INCOME = "Renda fixa é um tipo de investimento onde você 'empresta' dinheiro e recebe juros por isso. O Tesouro Selic é um dos mais seguros do Brasil — aqui simulamos como ele funciona."
        const val APPLIED_FEEDBACK = "Aplicação registrada! Aguarde a passagem do mês para ver seu dinheiro render."
        const val INSUFFICIENT_FOR_INVESTMENT = "Saldo insuficiente. Invista apenas o que não vai precisar no curto prazo."
        const val COMPOUND_INTEREST_TIP = "Dica: quanto mais tempo você manter o investimento, mais os juros compostos trabalham a seu favor."
        const val ACTIVE_INVESTMENTS_LABEL = "Aplicações ativas"
        const val NO_INVESTMENTS = "Você ainda não tem aplicações. Invista parte do seu saldo para vê-lo crescer!"
        const val INVESTED_AMOUNT_LABEL = "Valor aplicado"
        const val CURRENT_AMOUNT_LABEL = "Valor atual"
        const val EARNINGS_LABEL = "Rendimento"
    }

    // --- Resumo do mês ---
    object MonthlySummary {
        const val SCREEN_TITLE = "Resumo do mês"
        const val INCOME_RECEIVED = "Renda recebida"
        const val BILLS_PAID = "Contas pagas"
        const val AMOUNT_INVESTED = "Valor investido"
        const val TOTAL_WEALTH = "Patrimônio total"
        const val HEALTH_SCORE_LABEL = "Nota de saúde financeira"

        fun healthScoreMessage(score: Int): String = when {
            score >= 80 -> "Excelente! Você pagou suas contas, manteve reserva e investiu. Continue assim!"
            score >= 60 -> "Bom trabalho! Você está no caminho certo. Tente melhorar um pouco mais no próximo mês."
            score >= 40 -> "Você está progredindo. Foque em pagar todas as contas e formar uma reserva de emergência."
            score >= 20 -> "Atenção: seu mês financeiro teve dificuldades. Analise seus gastos e tente equilibrar melhor."
            else        -> "Não desanime! Todo mundo começa em algum lugar. O importante é aprender com esse mês e melhorar."
        }

        fun monthAdvancedMessage(snapshot: com.finsim.app.domain.model.MonthlySnapshot): String {
            val score = snapshot.financialHealthScore
            return "Mês ${snapshot.month - 1} encerrado! " + healthScoreMessage(score)
        }
    }

    // --- Erros gerais ---
    object General {
        const val GENERIC_ERROR = "Algo deu errado. Tente novamente."
        const val LOADING = "Carregando..."
        const val EMPTY_NAME = "Informe seu nome para continuar."
        const val INVALID_INCOME = "Informe uma renda válida (apenas números, maior que zero)."
    }
}
