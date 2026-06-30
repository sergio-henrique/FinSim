package com.finsim.app.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.AdvanceMonthUseCase
import com.finsim.app.application.usecase.GetMonthSummaryUseCase
import com.finsim.app.application.usecase.MonthSummary
import com.finsim.app.application.usecase.UseCaseResult
import com.finsim.app.domain.model.Achievement
import com.finsim.app.domain.model.Challenge
import com.finsim.app.domain.model.MarketEvent
import com.finsim.app.domain.model.RandomEvent
import com.finsim.app.simulation.missions.MissionCatalog
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
    val randomEvent: RandomEvent? = null,
    val marketEvent: MarketEvent? = null,
    val dividendsReceivedCents: Long = 0L,
    val newlyCompletedMissionTitles: List<String> = emptyList(),
    val newlyUnlockedAchievements: List<Achievement> = emptyList(),
    val completedChallenges: List<Challenge> = emptyList(),
    val failedChallenges: List<Challenge> = emptyList(),
)

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
                    val advanceResult = result.data
                    val score = advanceResult.snapshot.financialHealthScore
                    val missionTitles = advanceResult.newlyCompletedMissions.mapNotNull { id ->
                        MissionCatalog.getById(id)?.title
                    }
                    _uiState.update {
                        it.copy(
                            isAdvancingMonth = false,
                            monthAdvanceMessage = buildAdvanceMessage(score),
                            randomEvent = advanceResult.randomEvent,
                            marketEvent = advanceResult.marketEvent,
                            dividendsReceivedCents = advanceResult.dividendsReceivedCents,
                            newlyCompletedMissionTitles = missionTitles,
                            newlyUnlockedAchievements = advanceResult.newlyUnlockedAchievements,
                            completedChallenges = advanceResult.completedChallenges,
                            failedChallenges = advanceResult.failedChallenges,
                        )
                    }
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
        _uiState.update {
            it.copy(
                monthAdvanceMessage = null,
                randomEvent = null,
                marketEvent = null,
                dividendsReceivedCents = 0L,
                newlyCompletedMissionTitles = emptyList(),
                newlyUnlockedAchievements = emptyList(),
                completedChallenges = emptyList(),
                failedChallenges = emptyList(),
            )
        }
    }

    private fun buildAdvanceMessage(score: Int): String = when {
        score >= 71 -> "Mês encerrado! Saúde financeira: $score/100. Excelente! Continue mantendo suas contas em dia e investindo."
        score >= 41 -> "Mês encerrado! Saúde financeira: $score/100. Bom progresso! Tente pagar mais contas e construir sua reserva."
        else -> "Mês encerrado! Saúde financeira: $score/100. Atenção! Revise seus gastos e priorize pagar as contas essenciais."
    }
}
