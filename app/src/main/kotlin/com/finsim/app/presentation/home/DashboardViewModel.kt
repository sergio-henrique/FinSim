package com.finsim.app.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.AdvanceMonthUseCase
import com.finsim.app.application.usecase.GetMonthSummaryUseCase
import com.finsim.app.application.usecase.MonthSummary
import com.finsim.app.application.usecase.UseCaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val summary: MonthSummary? = null,
    val isLoading: Boolean = true,
    val isAdvancingMonth: Boolean = false,
    val monthAdvanceMessage: String? = null,
)

/**
 * ViewModel do Dashboard.
 *
 * Observa [GetMonthSummaryUseCase] de forma reativa e expõe
 * [advanceMonth] para acionar a passagem de mês simulado.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMonthSummaryUseCase: GetMonthSummaryUseCase,
    private val advanceMonthUseCase: AdvanceMonthUseCase,
) : ViewModel() {

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeSummary()
    }

    private fun observeSummary() {
        viewModelScope.launch {
            getMonthSummaryUseCase(profileId)
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { summary ->
                    _uiState.update { it.copy(summary = summary, isLoading = false) }
                }
        }
    }

    fun advanceMonth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAdvancingMonth = true) }

            when (val result = advanceMonthUseCase(profileId)) {
                is UseCaseResult.Success -> {
                    val snapshot = result.data
                    val score = snapshot.financialHealthScore
                    val message = buildAdvanceMessage(score)
                    _uiState.update { it.copy(isAdvancingMonth = false, monthAdvanceMessage = message) }
                }
                is UseCaseResult.Failure -> {
                    _uiState.update {
                        it.copy(isAdvancingMonth = false, monthAdvanceMessage = result.educationalMessage)
                    }
                }
            }
        }
    }

    fun clearMonthAdvanceMessage() {
        _uiState.update { it.copy(monthAdvanceMessage = null) }
    }

    private fun buildAdvanceMessage(score: Int): String = when {
        score >= 71 -> "Mês encerrado! Saúde financeira: $score/100. Excelente! Continue mantendo suas contas em dia e investindo."
        score >= 41 -> "Mês encerrado! Saúde financeira: $score/100. Bom progresso! Tente pagar mais contas e construir sua reserva."
        else -> "Mês encerrado! Saúde financeira: $score/100. Atenção! Revise seus gastos e priorize pagar as contas essenciais."
    }
}
