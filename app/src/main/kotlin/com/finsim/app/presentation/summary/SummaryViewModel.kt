package com.finsim.app.presentation.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.GetMonthSummaryUseCase
import com.finsim.app.application.usecase.MonthSummary
import com.finsim.app.domain.model.MonthlySnapshot
import com.finsim.app.domain.repository.MonthlySnapshotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SummaryUiState(
    val summary: MonthSummary? = null,
    val wealthHistory: List<MonthlySnapshot> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMonthSummaryUseCase: GetMonthSummaryUseCase,
    private val snapshotRepository: MonthlySnapshotRepository,
) : ViewModel() {

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getMonthSummaryUseCase(profileId),
                snapshotRepository.getAllByProfileId(profileId),
            ) { summary, snapshots ->
                Pair(summary, snapshots.sortedBy { it.month })
            }
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { (summary, snapshots) ->
                    _uiState.update { it.copy(summary = summary, wealthHistory = snapshots, isLoading = false) }
                }
        }
    }
}
