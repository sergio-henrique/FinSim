package com.finsim.app.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsim.app.domain.model.AgeRange
import com.finsim.app.presentation.common.EducationalMessage
import com.finsim.app.presentation.common.FinSimButton

/**
 * Tela de onboarding — primeiro contato do usuário com o FinSim.
 *
 * Coleta nome, faixa etária e renda mensal simulada.
 * Deixa claro desde o início que tudo é uma simulação educativa.
 */
@Composable
fun OnboardingScreen(
    onProfileCreated: (Long) -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.createdProfileId) {
        uiState.createdProfileId?.let { profileId ->
            onProfileCreated(profileId)
        }
    }

    if (uiState.isLoading) {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Bem-vindo ao FinSim",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            EducationalMessage(
                message = "Este é um jogo educativo. Os valores são virtuais e servem para você aprender como funciona a vida financeira. Nenhum dinheiro real é movimentado.",
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Como você quer se chamar?",
                style = MaterialTheme.typography.titleMedium,
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("Nome ou apelido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Qual é a sua faixa de idade?",
                style = MaterialTheme.typography.titleMedium,
            )

            AgeRangeSelector(
                selected = uiState.ageRange,
                onSelect = viewModel::updateAgeRange,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Qual será sua renda mensal simulada?",
                style = MaterialTheme.typography.titleMedium,
            )

            OutlinedTextField(
                value = uiState.monthlyIncome,
                onValueChange = viewModel::updateIncome,
                label = { Text("Renda em reais (ex: 1500)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("R$ ") },
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = "Este valor é fictício e será usado para criar situações de aprendizado.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(8.dp))

            FinSimButton(
                text = "Começar simulação",
                onClick = viewModel::createProfile,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )

            uiState.error?.let { errorMessage ->
                EducationalMessage(message = errorMessage)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AgeRangeSelector(
    selected: AgeRange,
    onSelect: (AgeRange) -> Unit,
) {
    val options = listOf(
        AgeRange.CHILD to "Criança (10–12)",
        AgeRange.TEEN to "Adolescente (13–15)",
        AgeRange.YOUNG to "Jovem (16–18)",
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (range, label) ->
            val isSelected = selected == range
            OutlinedButton(
                onClick = { onSelect(range) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
