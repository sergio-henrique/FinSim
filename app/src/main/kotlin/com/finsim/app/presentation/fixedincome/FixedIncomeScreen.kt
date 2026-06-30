package com.finsim.app.presentation.fixedincome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsim.app.domain.model.FixedIncomeInvestment
import com.finsim.app.domain.model.FixedIncomeProductType
import com.finsim.app.presentation.common.ContextualTipCard
import com.finsim.app.presentation.common.EducationalMessage
import com.finsim.app.presentation.common.FinSimButton
import com.finsim.app.presentation.common.FinSimCard
import com.finsim.app.presentation.common.toCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixedIncomeScreen(
    profileId: Long,
    onNavigateBack: () -> Unit,
    viewModel: FixedIncomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Renda Fixa", fontWeight = FontWeight.Bold) },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                ContextualTipCard(
                    emoji = "📊",
                    title = "Renda fixa: previsível e segura",
                    body = "Em renda fixa você sabe as regras de rendimento antes de investir. " +
                        "O Tesouro Selic rende próximo da taxa Selic e tem liquidez diária — " +
                        "você pode resgatar quando precisar sem perda. " +
                        "O CDB costuma render um percentual do CDI, que fica muito próximo da Selic.",
                )
            }

            // Seletor de produto
            item {
                Text(
                    text = "Escolha um produto:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ProductTab(
                        label = "Tesouro Selic",
                        isSelected = uiState.selectedProduct == FixedIncomeProductType.TESOURO_SELIC_SIMULADO,
                        onClick = { viewModel.selectProduct(FixedIncomeProductType.TESOURO_SELIC_SIMULADO) },
                        modifier = Modifier.weight(1f),
                    )
                    ProductTab(
                        label = "CDB",
                        isSelected = uiState.selectedProduct == FixedIncomeProductType.CDB_SIMULADO,
                        onClick = { viewModel.selectProduct(FixedIncomeProductType.CDB_SIMULADO) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Info do produto selecionado
            item {
                ProductInfoCard(product = uiState.selectedProduct)
            }

            // Saldo disponível
            item {
                uiState.account?.let { account ->
                    FinSimCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Saldo disponível",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            )
                            Text(
                                text = account.balance.toCurrency(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            // Campo de aplicação
            item {
                OutlinedTextField(
                    value = uiState.inputAmount,
                    onValueChange = viewModel::updateInputAmount,
                    label = { Text("Valor a aplicar (em reais)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    prefix = { Text("R$ ") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                FinSimButton(
                    text = "Aplicar",
                    onClick = viewModel::invest,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Lista de aplicações ativas
            if (uiState.investments.isNotEmpty()) {
                item {
                    Text(
                        text = "Suas aplicações",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                items(uiState.investments, key = { it.id }) { investment ->
                    InvestmentCard(investment = investment)
                }
            } else {
                item {
                    EducationalMessage(
                        message = "Você ainda não tem aplicações. Investir regularmente, mesmo que pequenos valores, faz diferença no longo prazo.",
                    )
                }
            }

            item {
                EducationalMessage(
                    message = "Simulação educativa. Os valores e taxas são fictícios e não representam investimentos reais ou recomendação financeira.",
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun ProductTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
        ),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ProductInfoCard(product: FixedIncomeProductType) {
    val (title, rate, liquidity, educational) = when (product) {
        FixedIncomeProductType.TESOURO_SELIC_SIMULADO -> ProductInfo(
            title = "Tesouro Selic Simulado",
            rate = "0,8% ao mês",
            liquidity = "Diária — resgate a qualquer mês",
            educational = "O Tesouro Selic real acompanha a taxa básica de juros (Selic) definida pelo Banco Central. É considerado um dos investimentos mais seguros do Brasil, pois é garantido pelo governo.",
        )
        FixedIncomeProductType.CDB_SIMULADO -> ProductInfo(
            title = "CDB Simulado",
            rate = "0,9% ao mês",
            liquidity = "Diária — resgate a qualquer mês",
            educational = "O CDB (Certificado de Depósito Bancário) é emitido por bancos e costuma pagar uma taxa um pouco maior que o Tesouro Selic. Em troca, você assume um pequeno risco do banco — por isso o retorno maior.",
        )
    }

    FinSimCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Taxa simulada: $rate", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Liquidez: $liquidity",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = educational,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            )
        }
    }
}

private data class ProductInfo(
    val title: String,
    val rate: String,
    val liquidity: String,
    val educational: String,
)

@Composable
private fun InvestmentCard(investment: FixedIncomeInvestment) {
    val earnings = investment.currentAmount - investment.investedAmount
    val productName = when (investment.productType) {
        FixedIncomeProductType.TESOURO_SELIC_SIMULADO -> "Tesouro Selic Simulado"
        FixedIncomeProductType.CDB_SIMULADO -> "CDB Simulado"
    }

    FinSimCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = productName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Aplicado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                    Text(
                        text = investment.investedAmount.toCurrency(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Valor atual",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                    Text(
                        text = investment.currentAmount.toCurrency(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            if (earnings > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rendimento: +${earnings.toCurrency()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Text(
                text = "Início: mês ${investment.startMonth} | ${investment.monthlyRateBps / 100.0}% ao mês",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        }
    }
}
