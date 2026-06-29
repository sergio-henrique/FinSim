package com.finsim.app.presentation.bills

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.GetMonthSummaryUseCase
import com.finsim.app.application.usecase.PayBillUseCase
import com.finsim.app.application.usecase.UseCaseResult
import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.Bill
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BillsUiState(
    val bills: List<Bill> = emptyList(),
    val account: Account? = null,
    val isLoading: Boolean = true,
    val message: String? = null,
)

/**
 * ViewModel da tela de contas do mês.
 *
 * Observa o resumo via [GetMonthSummaryUseCase] e delega o pagamento
 * ao [PayBillUseCase]. Não contém regras financeiras.
 */
@HiltViewModel
class BillsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMonthSummaryUseCase: GetMonthSummaryUseCase,
    private val payBillUseCase: PayBillUseCase,
) : ViewModel() {

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])

    private val _uiState = MutableStateFlow(BillsUiState())
    val uiState: StateFlow<BillsUiState> = _uiState.asStateFlow()

    init {
        observeSummary()
    }

    private fun observeSummary() {
        viewModelScope.launch {
            getMonthSummaryUseCase(profileId)
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { summary ->
                    _uiState.update {
                        it.copy(
                            bills = summary.currentMonthBills,
                            account = summary.account,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun payBill(bill: Bill) {
        val account = _uiState.value.account ?: return
        viewModelScope.launch {
            when (val result = payBillUseCase(account, bill)) {
                is UseCaseResult.Success -> {
                    _uiState.update {
                        it.copy(message = "Conta paga! Manter as contas em dia é essencial para uma vida financeira saudável.")
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
