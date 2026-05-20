package com.example.top.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.top.data.model.GroupSummary
import com.example.top.data.repository.FirebaseGroupRepository
import com.example.top.data.repository.GroupRepository
import com.example.top.firebase.service.RealtimeDatabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "User",
    val searchQuery: String = "",
    val createdGroups: List<GroupSummary> = emptyList(),
    val joinedGroups: List<GroupSummary> = emptyList(),
    val isInternetAvailable: Boolean = true,
    val isLoading: Boolean = true,
    val message: String? = null
)

class HomeViewModel(
    private val repository: GroupRepository = FirebaseGroupRepository(),
    private val dbService: RealtimeDatabaseService = RealtimeDatabaseService()
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var createdGroupsJob: Job? = null
    private var joinedGroupsJob: Job? = null
    private var startedForUserId: String? = null

    fun start(userId: String) {
        if (userId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, message = "Unable to load groups. Please login again.") }
            return
        }
        if (startedForUserId == userId) return
        startedForUserId = userId
        createdGroupsJob?.cancel()
        joinedGroupsJob?.cancel()
        _uiState.update { it.copy(isLoading = true) }
        observeCreatedGroups(userId)
        observeJoinedGroups(userId)
    }

    fun startForCurrentUser() {
        val uid = dbService.currentUserId()
        if (uid != null) start(uid) else _uiState.update {
            it.copy(isLoading = false, message = "No active session. Please login again.")
        }
    }

    private fun observeCreatedGroups(userId: String) {
        createdGroupsJob = viewModelScope.launch {
            repository.observeCreatedGroups(userId)
                .catch { _uiState.update { state -> state.copy(message = it.message ?: "Failed to load created groups", isLoading = false) } }
                .collect { groups ->
                    _uiState.update { it.copy(createdGroups = groups, isLoading = false) }
                }
        }
    }

    private fun observeJoinedGroups(userId: String) {
        joinedGroupsJob = viewModelScope.launch {
            repository.observeJoinedGroups(userId)
                .catch { _uiState.update { state -> state.copy(message = it.message ?: "Failed to load joined groups", isLoading = false) } }
                .collect { groups ->
                    _uiState.update { it.copy(joinedGroups = groups, isLoading = false) }
                }
        }
    }

    fun createGroup(groupName: String, description: String) {
        val uid = dbService.currentUserId() ?: return _uiState.update { it.copy(message = "Please login again") }
        if (groupName.trim().length < 3) return _uiState.update { it.copy(message = "Group name should be at least 3 letters") }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.createGroup(uid, _uiState.value.userName, groupName.trim(), description.trim())
                .onSuccess { _uiState.update { it.copy(isLoading = false, message = "Group created") } }
                .onFailure { _uiState.update { it.copy(isLoading = false, message = it.message ?: "Unable to create group") } }
        }
    }

    fun joinGroup(groupId: String) {
        val uid = dbService.currentUserId() ?: return _uiState.update { it.copy(message = "Please login again") }
        if (groupId.isBlank()) return _uiState.update { it.copy(message = "Enter valid group id") }
        viewModelScope.launch {
            repository.joinGroup(uid, groupId.trim())
                .onSuccess { _uiState.update { it.copy(message = "Group joined") } }
                .onFailure { _uiState.update { it.copy(message = it.message ?: "Unable to join group") } }
        }
    }

    fun setUserName(name: String) {
        _uiState.update { it.copy(userName = name.trim().ifBlank { "User" }) }
    }

    fun clearMessage() = _uiState.update { it.copy(message = null) }
}
