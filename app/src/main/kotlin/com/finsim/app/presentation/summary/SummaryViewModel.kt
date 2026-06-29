package com.finsim.app.presentation.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.GetMonthSummaryUseCase
import com.finsim.app.application.usecase.MonthSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SummaryUiState(
    val summary: MonthSummary? = null,
    val isLoading: Boolean = true,
)

/**
 * ViewModel da tela de resumo do mês.
 *
 * Carrega o [MonthSummary] mais recente via [GetMonthSummaryUseCase]
 * e o expõe de forma reativa para a tela de resumo.
 */
@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMonthSummaryUseCase: GetMonthSummaryUseCase,
) : ViewModel() {

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

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
}
