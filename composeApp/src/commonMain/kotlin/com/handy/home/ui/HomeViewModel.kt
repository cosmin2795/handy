package com.handy.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handy.auth.domain.model.AuthUser
import com.handy.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: AuthUser? = null,
)

class HomeViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(user = authRepository.currentUser()))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = HomeUiState()
        }
    }
}
