package com.handy.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handy.auth.domain.model.AuthResult
import com.handy.auth.domain.model.AuthUser
import com.handy.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: AuthUser? = null,
    val error: String? = null,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(user = authRepository.currentUser()))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.signInWithGoogle()) {
                is AuthResult.Success -> _uiState.value = AuthUiState(user = result.user)
                is AuthResult.Error -> _uiState.value = AuthUiState(error = result.message)
                AuthResult.Cancelled -> _uiState.value = AuthUiState()
            }
        }
    }

    fun signInWithFacebook() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.signInWithFacebook()) {
                is AuthResult.Success -> _uiState.value = AuthUiState(user = result.user)
                is AuthResult.Error -> _uiState.value = AuthUiState(error = result.message)
                AuthResult.Cancelled -> _uiState.value = AuthUiState()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState()
        }
    }
}
