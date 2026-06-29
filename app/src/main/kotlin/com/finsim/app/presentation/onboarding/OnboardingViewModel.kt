package com.finsim.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.CreateProfileUseCase
import com.finsim.app.application.usecase.GetExistingProfileUseCase
import com.finsim.app.application.usecase.UseCaseResult
import com.finsim.app.domain.model.AgeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val name: String = "",
    val monthlyIncome: String = "",
    val ageRange: AgeRange = AgeRange.TEEN,
    val isLoading: Boolean = true,
    val error: String? = null,
    val createdProfileId: Long? = null,
)

/**
 * ViewModel da tela de onboarding.
 *
 * No init, verifica se já existe um perfil salvo. Se sim, redireciona para
 * o Dashboard imediatamente sem exibir o formulário de cadastro.
 *
 * A conversão de String para Long centavos ocorre aqui pois é
 * responsabilidade da camada de apresentação adaptar input do usuário
 * ao contrato dos casos de uso.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val createProfileUseCase: CreateProfileUseCase,
    private val getExistingProfileUseCase: GetExistingProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        checkForExistingProfile()
    }

    private fun checkForExistingProfile() {
        viewModelScope.launch {
            val existing = getExistingProfileUseCase().first()
            if (existing != null) {
                _uiState.update { it.copy(isLoading = false, createdProfileId = existing.id) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value, error = null) }
    }

    fun updateIncome(value: String) {
        val filtered = value.filter { char -> char.isDigit() }
        _uiState.update { it.copy(monthlyIncome = filtered, error = null) }
    }

    fun updateAgeRange(value: AgeRange) {
        _uiState.update { it.copy(ageRange = value) }
    }

    fun createProfile() {
        val state = _uiState.value
        val incomeInCents = state.monthlyIncome.toLongOrNull()?.times(100L) ?: 0L

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = createProfileUseCase(state.name, state.ageRange, incomeInCents)) {
                is UseCaseResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, createdProfileId = result.data) }
                }
                is UseCaseResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, error = result.educationalMessage) }
                }
            }
        }
    }
}
