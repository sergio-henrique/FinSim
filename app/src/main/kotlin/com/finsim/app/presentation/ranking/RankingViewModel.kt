package com.finsim.app.presentation.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.GetRankingUseCase
import com.finsim.app.application.usecase.RankingEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RankingUiState(
    val entries: List<RankingEntry> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val getRankingUseCase: GetRankingUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getRankingUseCase.invoke()
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { entries ->
                    _uiState.update { it.copy(entries = entries, isLoading = false) }
                }
        }
    }
}
