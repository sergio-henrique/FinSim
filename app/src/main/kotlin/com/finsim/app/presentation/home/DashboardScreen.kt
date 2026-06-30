package com.finsim.app.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsim.app.domain.model.Bill
import com.finsim.app.presentation.common.FinSimButton
import com.finsim.app.presentation.common.FinSimCard
import com.finsim.app.presentation.common.toCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    profileId: Long,
    onNavigateToBills: () -> Unit,
    onNavigateToReserve: () -> Unit,
    onNavigateToFixedIncome: () -> Unit,
    onNavigateToSummary: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToStockMarket: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToRanking: () -> Unit,
    onNavigateToChallenges: () -> Unit,
    onNavigateToProfileSelector: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.monthAdvanceMessage?.let { message ->
        val randomEvent = uiState.randomEvent
        val marketEvent = uiState.marketEvent
        val missions = uiState.newlyCompletedMissionTitles
        val achievements = uiState.newlyUnlockedAchievements
        val completedChallenges = uiState.completedChallenges
        val failedChallenges = uiState.failedChallenges
        val dividends = uiState.dividendsReceivedCents
        val hasImprevisto = randomEvent != null
        val hasMarketEvent = marketEvent != null

        val title = when {
            hasImprevisto && hasMarketEvent -> "Fim do mês — Imprevisto + Mercado"
            hasImprevisto -> "Fim do mês — Imprevisto!"
            hasMarketEvent -> "Fim do mês — Evento de Mercado"
            else -> "Fim do mês"
        }

        AlertDialog(
            onDismissRequest = viewModel::clearMonthAdvanceMessage,
            title = { Text(title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (randomEvent != null) {
                        Text(
                            text = "⚠ ${randomEvent.title}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(text = randomEvent.description)
                        Text(
                            text = "Custo: R$ ${randomEvent.amountCents / 100}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                        Text(text = randomEvent.educationalMessage, style = MaterialTheme.typography.bodySmall)
                        HorizontalDivider()
                    }

                    if (marketEvent != null) {
                        Text(
                            text = "📈 ${marketEvent.title}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(text = marketEvent.description)
                        Text(text = marketEvent.educationalMessage, style = MaterialTheme.typography.bodySmall)
                        HorizontalDivider()
                    }

                    if (dividends > 0) {
                        Text(
                            text = "💰 Dividendos recebidos: R$ ${dividends / 100}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                        HorizontalDivider()
                    }

                    Text(text = message)

                    if (missions.isNotEmpty()) {
                        HorizontalDivider()
                        Text(
                            text = "Missões concluídas:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        missions.forEach { t -> Text("✓ $t", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
                    }

                    if (achievements.isNotEmpty()) {
                        HorizontalDivider()
                        Text(
                            text = "Conquistas desbloqueadas:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                        achievements.forEach { a -> Text("${a.emoji} ${a.title}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary) }
                    }

                    if (completedChallenges.isNotEmpty()) {
                        HorizontalDivider()
                        Text(
                            text = "Desafio concluído!",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                        completedChallenges.forEach { c ->
                            Text("${c.emoji} ${c.title}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                            Text(c.educationalMessage, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    if (failedChallenges.isNotEmpty()) {
                        HorizontalDivider()
                        Text(
                            text = "Desafio encerrado sem êxito:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error,
                        )
                        failedChallenges.forEach { c ->
                            Text("${c.emoji} ${c.title}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            Text("Não desanime — cada tentativa é um aprendizado. Tente novamente!", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::clearMonthAdvanceMessage) { Text("Entendido") }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = uiState.summary?.let { "Mês ${it.profile.currentMonth}" } ?: "FinSim"
                    Text(text = title, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->

        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val summary = uiState.summary ?: return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Olá, ${summary.profile.name}!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BalanceCard(
                    label = "Saldo em conta",
                    value = summary.account.balance.toCurrency(),
                    modifier = Modifier.weight(1f),
                )
                BalanceCard(
                    label = "Reserva de emergência",
                    value = summary.account.emergencyReserveBalance.toCurrency(),
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val investmentsTotal = summary.investments.sumOf { it.currentAmount }
                BalanceCard(
                    label = "Investimentos",
                    value = investmentsTotal.toCurrency(),
                    modifier = Modifier.weight(1f),
                )
                BalanceCard(
                    label = "Patrimônio total",
                    isHighlighted = true,
                    value = summary.totalWealth.toCurrency(),
                    modifier = Modifier.weight(1f),
                )
            }

            val pendingBills = summary.currentMonthBills.filter { !it.isPaid }.take(3)
            if (pendingBills.isNotEmpty()) {
                FinSimCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Contas pendentes",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        pendingBills.forEachIndexed { index, bill ->
                            PendingBillRow(bill = bill)
                            if (index < pendingBills.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                        if (summary.currentMonthBills.count { !it.isPaid } > 3) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = onNavigateToBills) {
                                Text("Ver todas as contas")
                            }
                        }
                    }
                }
            } else if (summary.currentMonthBills.isNotEmpty()) {
                FinSimCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Todas as contas do mês estão pagas!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }

            Text(
                text = "O que deseja fazer?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FinSimButton(
                    text = "Contas",
                    onClick = onNavigateToBills,
                    modifier = Modifier.weight(1f),
                )
                FinSimButton(
                    text = "Reserva",
                    onClick = onNavigateToReserve,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FinSimButton(
                    text = "Renda Fixa",
                    onClick = onNavigateToFixedIncome,
                    modifier = Modifier.weight(1f),
                )
                FinSimButton(
                    text = "Ações",
                    onClick = onNavigateToStockMarket,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FinSimButton(
                    text = "Resumo",
                    onClick = onNavigateToSummary,
                    modifier = Modifier.weight(1f),
                )
                FinSimButton(
                    text = "Missões",
                    onClick = onNavigateToProgress,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FinSimButton(
                    text = "Extrato",
                    onClick = onNavigateToHistory,
                    modifier = Modifier.weight(1f),
                )
                FinSimButton(
                    text = "Desafios",
                    onClick = onNavigateToChallenges,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FinSimButton(
                    text = "Ranking",
                    onClick = onNavigateToRanking,
                    modifier = Modifier.weight(1f),
                )
            }

            FinSimButton(
                text = "Trocar perfil",
                onClick = onNavigateToProfileSelector,
                modifier = Modifier.fillMaxWidth(),
            )

            FinSimButton(
                text = "Avançar mês",
                onClick = viewModel::advanceMonth,
                isLoading = uiState.isAdvancingMonth,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BalanceCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
) {
    FinSimCard(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun PendingBillRow(bill: Bill) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = bill.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = bill.amount.toCurrency(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}
