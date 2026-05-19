package com.example.top.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.top.data.model.UserProfile
import com.example.top.data.repository.AuthRepository
import com.example.top.data.repository.InMemoryAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: UserProfile? = null,
    val message: String? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel(
    private val repository: AuthRepository = InMemoryAuthRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(identifier: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            repository.login(identifier.trim(), password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(isLoading = false, currentUser = user, isLoggedIn = true, message = "Welcome ${user.name}")
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, message = error.message) }
                }
        }
    }

    fun createAccount(profile: UserProfile, password: String, confirmPassword: String, onSuccess: () -> Unit) {
        if (password != confirmPassword) {
            _uiState.update { it.copy(message = "Passwords do not match.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            repository.createAccount(profile, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(isLoading = false, currentUser = user, isLoggedIn = true, message = "Account created")
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, message = error.message) }
                }
        }
    }

    fun recoverPassword(
        identifier: String,
        firstPetName: String,
        hasNoPet: Boolean,
        firstSchoolName: String,
        firstFriendName: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            repository.recoverPassword(identifier, firstPetName, hasNoPet, firstSchoolName, firstFriendName)
                .onSuccess {
                    _uiState.update { state -> state.copy(isLoading = false, message = "Recovery details matched. Firebase reset comes next.") }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, message = error.message) }
                }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(message = null) }
}
