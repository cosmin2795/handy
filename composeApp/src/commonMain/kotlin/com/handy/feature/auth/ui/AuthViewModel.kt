package com.handy.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handy.feature.auth.domain.model.AuthResult
import com.handy.feature.auth.domain.model.AuthUser
import com.handy.feature.auth.domain.repository.AuthRepository
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

    /** Called by the platform launcher once the Google ID token is obtained. */
    fun onGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.signInWithGoogle(idToken)) {
                is AuthResult.Success -> _uiState.value = AuthUiState(user = result.user)
                is AuthResult.Error -> _uiState.value = AuthUiState(error = result.message)
                AuthResult.Cancelled -> _uiState.value = AuthUiState()
            }
        }
    }

    /** Called by the platform launcher once the Facebook access token is obtained. */
    fun onFacebookSignIn(accessToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = authRepository.signInWithFacebook(accessToken)) {
                is AuthResult.Success -> _uiState.value = AuthUiState(user = result.user)
                is AuthResult.Error -> _uiState.value = AuthUiState(error = result.message)
                AuthResult.Cancelled -> _uiState.value = AuthUiState()
            }
        }
    }

    /** Called by a launcher when the user dismisses the sign-in dialog. */
    fun onSignInCancelled() {
        _uiState.value = _uiState.value.copy(isLoading = false, error = null)
    }

    /** Called by a launcher when the sign-in flow itself errors before a token is produced. */
    fun onSignInError(message: String) {
        _uiState.value = AuthUiState(error = message)
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState()
        }
    }
}
