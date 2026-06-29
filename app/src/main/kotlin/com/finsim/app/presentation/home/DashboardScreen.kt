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

/**
 * Tela principal do FinSim — Dashboard.
 *
 * Exibe saldo, reserva de emergência, investimentos e patrimônio total.
 * Permite avançar o mês simulado e navegar para as demais seções do MVP.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    profileId: Long,
    onNavigateToBills: () -> Unit,
    onNavigateToReserve: () -> Unit,
    onNavigateToFixedIncome: () -> Unit,
    onNavigateToSummary: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.monthAdvanceMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::clearMonthAdvanceMessage,
            title = { Text("Fim do mês") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = viewModel::clearMonthAdvanceMessage) {
                    Text("Entendido")
                }
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
                    text = "Resumo",
                    onClick = onNavigateToSummary,
                    modifier = Modifier.weight(1f),
                )
            }

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
