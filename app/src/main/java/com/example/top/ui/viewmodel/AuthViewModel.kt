package com.example.top.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.top.TopApplication
import com.example.top.data.model.UserProfile
import com.example.top.data.repository.FirebaseAuthRepository
import com.example.top.ui.state.AuthState
import com.example.top.util.ConnectivityObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: UserProfile? = null,
    val message: String? = null,
    val authState: AuthState = AuthState.Loading,
    val isInternetAvailable: Boolean = true
)

class AuthViewModel(
) : ViewModel() {

    private val repository by lazy { FirebaseAuthRepository() }

    private val connectivityObserver = ConnectivityObserver(TopApplication.instance)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeConnectivity()
        checkSession()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.observe().collect { connected ->
                _uiState.update { it.copy(isInternetAvailable = connected) }
            }
        }
    }

    fun checkSession() {
        val hasSession = runCatching { repository.hasSession() }.getOrElse {
            _uiState.update { state -> state.copy(authState = AuthState.Unauthenticated, isLoading = false, message = "Firebase is not initialized. Check google-services.json setup.") }
            false
        }
        if (!hasSession) {
            _uiState.update { state ->
                state.copy(
                    authState = AuthState.Unauthenticated,
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authState = AuthState.Loading) }
            repository.getLoggedInUser()
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(isLoading = false, currentUser = user, authState = AuthState.Authenticated)
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isLoading = false, authState = AuthState.Unauthenticated, message = "Session expired. Please login again.")
                    }
                }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.length < 6) {
            _uiState.update { it.copy(message = "Enter valid email and password (min 6).") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            repository.login(email, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(isLoading = false, currentUser = user, authState = AuthState.Authenticated, message = "Welcome ${user.name}")
                    }
                    onSuccess()
                }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, message = error.message) } }
        }
    }

    fun createAccount(
        profile: UserProfile,
        email: String,
        password: String,
        confirmPassword: String,
        profileImageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        when {
            profile.name.isBlank() -> _uiState.update { it.copy(message = "Name is required") }
            email.isBlank() -> _uiState.update { it.copy(message = "Email is required") }
            profile.phoneNumber.length < 10 -> _uiState.update { it.copy(message = "Enter valid phone") }
            password != confirmPassword -> _uiState.update { it.copy(message = "Passwords do not match") }
            password.length < 6 -> _uiState.update { it.copy(message = "Password should be at least 6 characters") }
            else -> viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, message = null) }
                repository.createAccount(profile, email, password, profileImageUri)
                    .onSuccess { user ->
                        _uiState.update {
                            it.copy(isLoading = false, currentUser = user, authState = AuthState.Authenticated, message = "Account created")
                        }
                        onSuccess()
                    }
                    .onFailure { error -> _uiState.update { it.copy(isLoading = false, message = error.message) } }
            }
        }
    }

    fun recoverPassword(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(message = "Email is required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            repository.sendPasswordReset(email)
                .onSuccess { _uiState.update { it.copy(isLoading = false, message = "Password reset email sent") } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, message = error.message) } }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.update { it.copy(currentUser = null, authState = AuthState.Unauthenticated) }
    }

    fun clearMessage() = _uiState.update { it.copy(message = null) }
}
