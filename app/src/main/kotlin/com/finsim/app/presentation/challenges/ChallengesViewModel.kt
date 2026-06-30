package com.finsim.app.presentation.challenges

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.ChallengeWithProgress
import com.finsim.app.application.usecase.GetChallengesUseCase
import com.finsim.app.application.usecase.StartChallengeUseCase
import com.finsim.app.application.usecase.UseCaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChallengesUiState(
    val challenges: List<ChallengeWithProgress> = emptyList(),
    val isLoading: Boolean = true,
    val feedbackMessage: String? = null,
)

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChallengesUseCase: GetChallengesUseCase,
    private val startChallengeUseCase: StartChallengeUseCase,
) : ViewModel() {

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])

    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getChallengesUseCase.invoke(profileId)
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { list ->
                    _uiState.update { it.copy(challenges = list, isLoading = false) }
                }
        }
    }

    fun startChallenge(challengeId: String) {
        viewModelScope.launch {
            when (val result = startChallengeUseCase(profileId, challengeId)) {
                is UseCaseResult.Success -> _uiState.update { it.copy(feedbackMessage = "Desafio iniciado! Boa sorte!") }
                is UseCaseResult.Failure -> _uiState.update { it.copy(feedbackMessage = result.educationalMessage) }
            }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
