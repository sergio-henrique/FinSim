package com.finsim.app.presentation.stockmarket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.BuyStockUseCase
import com.finsim.app.application.usecase.GetPortfolioUseCase
import com.finsim.app.application.usecase.Portfolio
import com.finsim.app.application.usecase.SellStockUseCase
import com.finsim.app.application.usecase.UseCaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StockMarketUiState(
    val portfolio: Portfolio? = null,
    val isLoading: Boolean = true,
    val isBuying: Boolean = false,
    val isSelling: Boolean = false,
    val message: String? = null,
    val selectedTicker: String? = null,
    val quantityInput: String = "1",
)

@HiltViewModel
class StockMarketViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPortfolioUseCase: GetPortfolioUseCase,
    private val buyStockUseCase: BuyStockUseCase,
    private val sellStockUseCase: SellStockUseCase,
) : ViewModel() {

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])

    private val _uiState = MutableStateFlow(StockMarketUiState())
    val uiState: StateFlow<StockMarketUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getPortfolioUseCase(profileId)
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { portfolio ->
                    _uiState.update { it.copy(portfolio = portfolio, isLoading = false) }
                }
        }
    }

    fun selectTicker(ticker: String) {
        _uiState.update { it.copy(selectedTicker = ticker, quantityInput = "1") }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedTicker = null, quantityInput = "1") }
    }

    fun onQuantityChanged(value: String) {
        _uiState.update { it.copy(quantityInput = value) }
    }

    fun buy() {
        val ticker = _uiState.value.selectedTicker ?: return
        val quantity = _uiState.value.quantityInput.toIntOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isBuying = true) }
            when (val result = buyStockUseCase(profileId, ticker, quantity)) {
                is UseCaseResult.Success ->
                    _uiState.update {
                        it.copy(
                            isBuying = false,
                            selectedTicker = null,
                            message = "Compra realizada! Você agora possui ${result.data.quantity} ação(ões) de $ticker.",
                        )
                    }
                is UseCaseResult.Failure ->
                    _uiState.update { it.copy(isBuying = false, message = result.educationalMessage) }
            }
        }
    }

    fun sell() {
        val ticker = _uiState.value.selectedTicker ?: return
        val quantity = _uiState.value.quantityInput.toIntOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSelling = true) }
            when (val result = sellStockUseCase(profileId, ticker, quantity)) {
                is UseCaseResult.Success -> {
                    val r = result.data
                    val gainLossLabel = if (r.isProfit) "lucro" else "prejuízo"
                    val amount = Math.abs(r.gainLoss) / 100
                    _uiState.update {
                        it.copy(
                            isSelling = false,
                            selectedTicker = null,
                            message = "Venda realizada com $gainLossLabel de R$ $amount.",
                        )
                    }
                }
                is UseCaseResult.Failure ->
                    _uiState.update { it.copy(isSelling = false, message = result.educationalMessage) }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
