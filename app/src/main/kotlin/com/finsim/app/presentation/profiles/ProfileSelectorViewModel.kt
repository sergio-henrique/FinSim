package com.finsim.app.presentation.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsim.app.application.usecase.DeleteProfileUseCase
import com.finsim.app.domain.model.UserProfile
import com.finsim.app.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileSelectorUiState(
    val profiles: List<UserProfile> = emptyList(),
    val isLoading: Boolean = true,
    val confirmDeleteProfile: UserProfile? = null,
)

@HiltViewModel
class ProfileSelectorViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val deleteProfileUseCase: DeleteProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSelectorUiState())
    val uiState: StateFlow<ProfileSelectorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userProfileRepository.getAll()
                .catch { _uiState.update { it.copy(isLoading = false) } }
                .collect { profiles ->
                    _uiState.update { it.copy(profiles = profiles, isLoading = false) }
                }
        }
    }

    fun requestDelete(profile: UserProfile) {
        _uiState.update { it.copy(confirmDeleteProfile = profile) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(confirmDeleteProfile = null) }
    }

    fun confirmDelete() {
        val profile = _uiState.value.confirmDeleteProfile ?: return
        viewModelScope.launch {
            deleteProfileUseCase(profile.id)
            _uiState.update { it.copy(confirmDeleteProfile = null) }
        }
    }
}
