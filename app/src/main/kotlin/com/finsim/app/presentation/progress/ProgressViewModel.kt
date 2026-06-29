package com.finsim.app.presentation.progress

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.GetProgressUseCase
import com.finsim.app.application.usecase.ProgressSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressUiState(
    val summary: ProgressSummary? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProgressUseCase: GetProgressUseCase,
) : ViewModel() {

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState

    init {
        viewModelScope.launch {
            getProgressUseCase(profileId).collect { summary ->
                _uiState.update { it.copy(summary = summary, isLoading = false) }
            }
        }
    }
}
