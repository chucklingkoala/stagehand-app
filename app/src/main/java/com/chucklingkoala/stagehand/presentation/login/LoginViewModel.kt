package com.chucklingkoala.stagehand.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chucklingkoala.stagehand.data.local.TokenManager
import com.chucklingkoala.stagehand.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val loginSuccess: SharedFlow<Unit> = _loginSuccess.asSharedFlow()

    init {
        _state.update { it.copy(serverUrl = tokenManager.getServerUrl()) }
    }

    fun login(serverUrl: String, username: String, password: String) {
        if (serverUrl.isBlank() || username.isBlank() || password.isBlank()) {
            _state.update { it.copy(error = "All fields are required") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.login(serverUrl.trim(), username.trim(), password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _loginSuccess.emit(Unit)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Login failed"
                        )
                    }
                }
        }
    }
}
