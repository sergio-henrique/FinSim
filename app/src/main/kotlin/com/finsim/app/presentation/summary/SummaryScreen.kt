package com.finsim.app.presentation.summary

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsim.app.presentation.common.EducationalMessage
import com.finsim.app.presentation.common.FinSimCard
import com.finsim.app.presentation.common.toCurrency

// Cores definidas localmente para evitar dependência de vals internal do tema
private val HealthGreen = Color(0xFF34A853)
private val HealthOrange = Color(0xFFFBBC04)

/**
 * Tela de resumo do mês.
 *
 * Exibe um panorama completo do mês atual: renda, contas, investimentos,
 * patrimônio e nota de saúde financeira com barra de progresso colorida.
 * A mensagem educativa é personalizada com base na nota obtida.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    profileId: Long,
    onNavigateBack: () -> Unit,
    viewModel: SummaryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumo do mês", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
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
        val snapshot = summary.latestSnapshot
        val investmentsTotal = summary.investments.sumOf { it.currentAmount }
        val billsPaid = summary.currentMonthBills.filter { it.isPaid }
        val billsPaidTotal = billsPaid.sumOf { it.amount }
        val billsTotal = summary.currentMonthBills.sumOf { it.amount }

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
                text = "Mês ${summary.profile.currentMonth}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            // Renda do mês
            FinSimCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SummaryRow(label = "Renda mensal simulada", value = summary.profile.monthlyIncome.toCurrency())
                    SummaryRow(label = "Saldo em conta", value = summary.account.balance.toCurrency())
                    SummaryRow(label = "Reserva de emergência", value = summary.account.emergencyReserveBalance.toCurrency())
                }
            }

            // Contas
            FinSimCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Contas",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    SummaryRow(label = "Pagas", value = billsPaidTotal.toCurrency())
                    SummaryRow(label = "Total do mês", value = billsTotal.toCurrency())
                    SummaryRow(
                        label = "Pendentes",
                        value = (billsTotal - billsPaidTotal).toCurrency(),
                    )
                }
            }

            // Investimentos e patrimônio
            FinSimCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SummaryRow(label = "Total investido (renda fixa)", value = investmentsTotal.toCurrency())
                    SummaryRow(
                        label = "Patrimônio total",
                        value = summary.totalWealth.toCurrency(),
                        valueColor = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            // Nota de saúde financeira
            if (snapshot != null) {
                HealthScoreCard(score = snapshot.financialHealthScore)
            } else {
                EducationalMessage(
                    message = "Avance o mês para receber sua nota de saúde financeira e dicas personalizadas.",
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
        )
    }
}

@Composable
private fun HealthScoreCard(score: Int) {
    val (barColor, statusLabel, educationalMessage) = when {
        score >= 71 -> Triple(
            HealthGreen,
            "Excelente",
            "Parabéns! Você está pagando suas contas, mantendo reserva e investindo. Continue assim para construir um futuro financeiro sólido.",
        )
        score >= 41 -> Triple(
            HealthOrange,
            "Bom progresso",
            "Você está no caminho certo! Tente pagar mais contas em dia e aumentar sua reserva de emergência para melhorar sua nota.",
        )
        else -> Triple(
            Color(0xFFE8710A),
            "Atenção necessária",
            "Ainda há muito a melhorar. Priorize pagar as contas essenciais, evite gastos desnecessários e comece a construir sua reserva de emergência.",
        )
    }

    FinSimCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Saúde financeira",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = barColor,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "$score / 100",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = barColor,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier.fillMaxWidth().height(10.dp),
                color = barColor,
                trackColor = barColor.copy(alpha = 0.2f),
            )
            Spacer(modifier = Modifier.height(12.dp))
            EducationalMessage(message = educationalMessage)
        }
    }
}
