package com.example.pangeaapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pangeaapp.data.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val session = authRepository.login(identifier, password)
                sessionManager.saveSession(session.jwt, session.user)
                _loginState.value = AuthState.Success
            } catch (e: AuthException) {
                _loginState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            try {
                val session = authRepository.register(username, email, password)
                sessionManager.saveSession(session.jwt, session.user)
                _registerState.value = AuthState.Success
            } catch (e: AuthException) {
                _registerState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun forgotPassword(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.forgotPassword(email)
                onSuccess()
            } catch (e: AuthException) {
                // Handle error
            }
        }
    }
}
