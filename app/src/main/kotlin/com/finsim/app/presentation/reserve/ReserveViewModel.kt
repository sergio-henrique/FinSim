package com.finsim.app.presentation.reserve

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.GetMonthSummaryUseCase
import com.finsim.app.application.usecase.TransferToReserveUseCase
import com.finsim.app.application.usecase.UseCaseResult
import com.finsim.app.domain.model.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReserveUiState(
    val account: Account? = null,
    val monthlyIncome: Long = 0L,
    val inputAmount: String = "",
    val isLoading: Boolean = true,
    val message: String? = null,
)

/**
 * ViewModel da tela de reserva de emergência.
 *
 * Observa o estado da conta via [GetMonthSummaryUseCase] e delega
 * a transferência ao [TransferToReserveUseCase].
 */
@HiltViewModel
class ReserveViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMonthSummaryUseCase: GetMonthSummaryUseCase,
    private val transferToReserveUseCase: TransferToReserveUseCase,
) : ViewModel() {

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])

    private val _uiState = MutableStateFlow(ReserveUiState())
    val uiState: StateFlow<ReserveUiState> = _uiState.asStateFlow()

    private var currentMonth: Int = 1

    init {
        observeSummary()
    }

    private fun observeSummary() {
        viewModelScope.launch {
            getMonthSummaryUseCase(profileId)
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { summary ->
                    currentMonth = summary.profile.currentMonth
                    _uiState.update {
                        it.copy(
                            account = summary.account,
                            monthlyIncome = summary.profile.monthlyIncome,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun updateInputAmount(value: String) {
        val filtered = value.filter { it.isDigit() }
        _uiState.update { it.copy(inputAmount = filtered, message = null) }
    }

    fun transferToReserve() {
        val account = _uiState.value.account ?: return
        val amountInCents = _uiState.value.inputAmount.toLongOrNull()?.times(100L) ?: 0L

        viewModelScope.launch {
            when (val result = transferToReserveUseCase(account, amountInCents, currentMonth)) {
                is UseCaseResult.Success -> {
                    _uiState.update {
                        it.copy(
                            inputAmount = "",
                            message = "Transferência realizada! Sua reserva de emergência está crescendo.",
                        )
                    }
                }
                is UseCaseResult.Failure -> {
                    _uiState.update { it.copy(message = result.educationalMessage) }
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
