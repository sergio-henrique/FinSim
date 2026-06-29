package com.finsim.app.presentation.fixedincome

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.GetMonthSummaryUseCase
import com.finsim.app.application.usecase.InvestInFixedIncomeUseCase
import com.finsim.app.application.usecase.UseCaseResult
import com.finsim.app.domain.model.Account
import com.finsim.app.domain.model.FixedIncomeInvestment
import com.finsim.app.domain.model.FixedIncomeProductType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FixedIncomeUiState(
    val account: Account? = null,
    val investments: List<FixedIncomeInvestment> = emptyList(),
    val inputAmount: String = "",
    val selectedProduct: FixedIncomeProductType = FixedIncomeProductType.TESOURO_SELIC_SIMULADO,
    val isLoading: Boolean = true,
    val message: String? = null,
)

@HiltViewModel
class FixedIncomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMonthSummaryUseCase: GetMonthSummaryUseCase,
    private val investInFixedIncomeUseCase: InvestInFixedIncomeUseCase,
) : ViewModel() {

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])

    private val _uiState = MutableStateFlow(FixedIncomeUiState())
    val uiState: StateFlow<FixedIncomeUiState> = _uiState.asStateFlow()

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
                            investments = summary.investments,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun selectProduct(product: FixedIncomeProductType) {
        _uiState.update { it.copy(selectedProduct = product, message = null) }
    }

    fun updateInputAmount(value: String) {
        val filtered = value.filter { it.isDigit() }
        _uiState.update { it.copy(inputAmount = filtered, message = null) }
    }

    fun invest() {
        val account = _uiState.value.account ?: return
        val amountInCents = _uiState.value.inputAmount.toLongOrNull()?.times(100L) ?: 0L
        val product = _uiState.value.selectedProduct

        viewModelScope.launch {
            when (val result = investInFixedIncomeUseCase(account, amountInCents, profileId, currentMonth, product)) {
                is UseCaseResult.Success -> {
                    _uiState.update {
                        it.copy(
                            inputAmount = "",
                            message = "Aplicação realizada! Seu dinheiro começa a render a partir do próximo mês.",
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
