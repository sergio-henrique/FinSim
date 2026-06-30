package com.finsim.app.presentation.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.domain.model.Transaction
import com.finsim.app.domain.repository.AccountRepository
import com.finsim.app.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionHistoryUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val profileId: Long = checkNotNull(savedStateHandle["profileId"])

    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepository.getByProfileId(profileId)
                .filterNotNull()
                .flatMapLatest { account -> transactionRepository.getAllByAccountId(account.id) }
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { transactions ->
                    _uiState.update { it.copy(transactions = transactions.sortedByDescending { t -> t.month }, isLoading = false) }
                }
        }
    }
}
