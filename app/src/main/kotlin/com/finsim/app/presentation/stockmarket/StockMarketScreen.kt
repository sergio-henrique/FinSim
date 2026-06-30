package com.finsim.app.presentation.stockmarket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsim.app.application.usecase.Portfolio
import com.finsim.app.application.usecase.PortfolioItem
import com.finsim.app.domain.model.StockPriceHistory
import com.finsim.app.presentation.common.ContextualTipCard
import com.finsim.app.presentation.common.FinSimButton
import com.finsim.app.presentation.common.FinSimCard
import com.finsim.app.presentation.common.toCurrency
import com.finsim.app.presentation.component.LineChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockMarketScreen(
    onBack: () -> Unit,
    viewModel: StockMarketViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.message?.let { msg ->
        AlertDialog(
            onDismissRequest = viewModel::clearMessage,
            title = { Text("Informação") },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = viewModel::clearMessage) { Text("Ok") }
            },
        )
    }

    uiState.selectedTicker?.let { ticker ->
        val item = uiState.portfolio?.items?.find { it.asset.ticker == ticker }
        if (item != null) {
            TradeDialog(
                item = item,
                quantityInput = uiState.quantityInput,
                isBuying = uiState.isBuying,
                isSelling = uiState.isSelling,
                priceHistory = uiState.priceHistory,
                onQuantityChanged = viewModel::onQuantityChanged,
                onBuy = viewModel::buy,
                onSell = viewModel::sell,
                onDismiss = viewModel::clearSelection,
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Renda Variável") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val portfolio = uiState.portfolio ?: return@Scaffold

        var selectedTab by remember { mutableIntStateOf(0) }
        val tabs = listOf("Mercado", "Minha carteira", "Desempenho")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                    )
                }
            }

            when (selectedTab) {
                0 -> MarketTab(portfolio = portfolio, onSelect = viewModel::selectTicker)
                1 -> PortfolioTab(portfolio = portfolio, onSelect = viewModel::selectTicker)
                2 -> PerformanceTab(portfolio = portfolio)
            }
        }
    }
}

@Composable
private fun MarketTab(portfolio: Portfolio, onSelect: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            ContextualTipCard(
                emoji = "⚖️",
                title = "Renda variável: risco e oportunidade",
                body = "Ações são partes de empresas reais. Seu preço sobe e desce conforme o mercado. " +
                    "Nunca invista mais do que pode perder e diversifique entre setores diferentes. " +
                    "O longo prazo historicamente favorece quem mantém a calma em crises.",
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Ativos disponíveis",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
        }

        items(portfolio.items) { item ->
            AssetCard(item = item, onClick = { onSelect(item.asset.ticker) })
        }
    }
}

@Composable
private fun PortfolioTab(portfolio: Portfolio, onSelect: (String) -> Unit) {
    val myItems = portfolio.items.filter { it.holding != null }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (myItems.isEmpty()) {
            item {
                FinSimCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Você ainda não tem ações.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            "Acesse a aba Mercado e compre ações para começar a investir em renda variável.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
            return@LazyColumn
        }

        item {
            PortfolioSummaryCard(portfolio)
            Spacer(Modifier.height(8.dp))
        }

        items(myItems) { item ->
            PortfolioItemCard(item = item, onClick = { onSelect(item.asset.ticker) })
        }
    }
}

@Composable
private fun PerformanceTab(portfolio: Portfolio) {
    val myItems = portfolio.items.filter { it.holding != null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (myItems.isEmpty()) {
            FinSimCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Nenhuma posição aberta",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        "Compre ações para ver o desempenho da sua carteira aqui.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            return@Column
        }

        // Resumo geral
        val totalInvested = portfolio.totalInvestedCents
        val totalMarket = portfolio.totalMarketValueCents
        val totalReturn = if (totalInvested > 0) {
            (portfolio.totalUnrealizedGainLoss.toDouble() / totalInvested.toDouble()) * 100.0
        } else 0.0
        val isPositive = portfolio.totalUnrealizedGainLoss >= 0

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Desempenho da carteira",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Total investido", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(totalInvested.toCurrency(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Valor atual", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(totalMarket.toCurrency(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Rentabilidade", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(
                            "${if (isPositive) "+" else ""}${"%.2f".format(totalReturn)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    if (isPositive)
                        "Boa! Sua carteira está valorizando. Lembre-se: rentabilidade passada não garante resultados futuros."
                    else
                        "Sua carteira está desvalorizada no momento. Isso é normal em renda variável — o importante é o longo prazo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        // Posição por posição
        Text(
            "Por ativo",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )
        myItems.forEach { item ->
            PerformanceItemCard(item)
        }

        // Mensagem educativa
        FinSimCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("💡 Diversificação", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Investir em setores diferentes reduz o risco. Se um setor cai, outro pode subir e compensar as perdas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PerformanceItemCard(item: PortfolioItem) {
    val holding = item.holding ?: return
    val gainLoss = item.unrealizedGainLoss
    val isProfit = gainLoss >= 0
    val returnPct = if (holding.totalInvestedCents > 0) {
        (gainLoss.toDouble() / holding.totalInvestedCents.toDouble()) * 100.0
    } else 0.0

    FinSimCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(item.asset.ticker, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(item.asset.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    "${if (isProfit) "+" else ""}${"%.2f".format(returnPct)}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isProfit) Color(0xFF2E7D32) else Color(0xFFC62828),
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Custo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(holding.totalInvestedCents.toCurrency(), style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Valor atual", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(item.marketValueCents.toCurrency(), style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Lucro/Prejuízo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${if (isProfit) "+" else ""}${gainLoss.toCurrency()}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isProfit) Color(0xFF2E7D32) else Color(0xFFC62828),
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioSummaryCard(portfolio: Portfolio) {
    val gainLoss = portfolio.totalUnrealizedGainLoss
    val isPositive = gainLoss >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Carteira de ações",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        "Valor de mercado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        portfolio.totalMarketValueCents.toCurrency(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Lucro/Prejuízo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        "${if (isPositive) "+" else ""}${gainLoss.toCurrency()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetCard(item: PortfolioItem, onClick: () -> Unit) {
    val price = item.price
    val changePct = price?.priceChangePct ?: 0.0
    val isUp = changePct >= 0

    FinSimCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            item.asset.ticker,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            item.asset.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        sectorLabel(item.asset.sector.name),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        (price?.currentPriceCents ?: item.asset.basePriceCents).toCurrency(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (price != null && price.lastUpdatedMonth > 0) {
                        Text(
                            "${if (isUp) "▲" else "▼"} ${"%.1f".format(Math.abs(changePct * 100))}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUp) Color(0xFF2E7D32) else Color(0xFFC62828),
                        )
                    }
                }
            }
            if (item.asset.monthlyDividendYield > 0) {
                Text(
                    "Dividendo mensal: ${"%.1f".format(item.asset.monthlyDividendYield * 100)}% a.m.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text(if (item.holding != null) "Comprar / Vender" else "Comprar")
            }
        }
    }
}

@Composable
private fun PortfolioItemCard(item: PortfolioItem, onClick: () -> Unit) {
    val gainLoss = item.unrealizedGainLoss
    val isProfit = gainLoss >= 0
    val holding = item.holding ?: return

    FinSimCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${item.asset.ticker} — ${item.asset.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "${holding.quantity} ações",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Preço médio", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(holding.averagePriceCents.toCurrency(), style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Valor atual", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(item.marketValueCents.toCurrency(), style = MaterialTheme.typography.bodySmall)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Lucro/Prejuízo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${if (isProfit) "+" else ""}${gainLoss.toCurrency()}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isProfit) Color(0xFF2E7D32) else Color(0xFFC62828),
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text("Comprar / Vender")
            }
        }
    }
}

@Composable
private fun TradeDialog(
    item: PortfolioItem,
    quantityInput: String,
    isBuying: Boolean,
    isSelling: Boolean,
    priceHistory: List<StockPriceHistory>,
    onQuantityChanged: (String) -> Unit,
    onBuy: () -> Unit,
    onSell: () -> Unit,
    onDismiss: () -> Unit,
) {
    val currentPrice = item.price?.currentPriceCents ?: item.asset.basePriceCents
    val quantity = quantityInput.toIntOrNull() ?: 0
    val totalCost = currentPrice * quantity

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${item.asset.ticker} — ${item.asset.name}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(item.asset.description, style = MaterialTheme.typography.bodySmall)

                // Gráfico de histórico de preços
                if (priceHistory.size >= 2) {
                    HorizontalDivider()
                    Text(
                        "Histórico de preços (${priceHistory.size} meses)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val chartColor = if ((priceHistory.last().priceCents) >= (priceHistory.first().priceCents))
                        Color(0xFF2E7D32) else Color(0xFFC62828)
                    LineChart(
                        points = priceHistory.map { it.priceCents.toFloat() },
                        lineColor = chartColor,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    val firstPrice = priceHistory.first().priceCents
                    val lastPrice = priceHistory.last().priceCents
                    val totalChangePct = if (firstPrice > 0) ((lastPrice - firstPrice).toDouble() / firstPrice.toDouble()) * 100.0 else 0.0
                    Text(
                        "Variação total: ${if (totalChangePct >= 0) "+" else ""}${"%.1f".format(totalChangePct)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (totalChangePct >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                    )
                }

                HorizontalDivider()
                Text(
                    "Preço atual: ${currentPrice.toCurrency()}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                item.holding?.let { h ->
                    Text(
                        "Você possui: ${h.quantity} ação(ões) — Preço médio: ${h.averagePriceCents.toCurrency()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                OutlinedTextField(
                    value = quantityInput,
                    onValueChange = onQuantityChanged,
                    label = { Text("Quantidade") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (quantity > 0) {
                    Text(
                        "Total: ${totalCost.toCurrency()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (item.holding != null) {
                    FinSimButton(
                        text = "Vender",
                        onClick = onSell,
                        isLoading = isSelling,
                        modifier = Modifier,
                    )
                }
                FinSimButton(
                    text = "Comprar",
                    onClick = onBuy,
                    isLoading = isBuying,
                    modifier = Modifier,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}

private fun sectorLabel(sector: String): String = when (sector) {
    "ENERGY" -> "Energia"
    "FINANCE" -> "Financeiro"
    "TECHNOLOGY" -> "Tecnologia"
    "CONSUMER_GOODS" -> "Consumo"
    "MINING" -> "Mineração"
    else -> sector
}
