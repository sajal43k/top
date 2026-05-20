package com.example.top.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.top.data.model.GroupSummary
import com.example.top.data.repository.DemoGroupRepository
import com.example.top.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "User",
    val searchQuery: String = "",
    val createdGroups: List<GroupSummary> = emptyList(),
    val joinedGroups: List<GroupSummary> = emptyList(),
    val isInternetAvailable: Boolean = true
)

class HomeViewModel(
    private val repository: GroupRepository = DemoGroupRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            val created = repository.getCreatedGroups("demo").getOrDefault(emptyList())
            val joined = repository.getJoinedGroups("demo").getOrDefault(emptyList())
            _uiState.update { it.copy(createdGroups = created, joinedGroups = joined) }
        }
    }

    fun setUserName(name: String) {
        _uiState.update { it.copy(userName = name.trim().ifBlank { "User" }) }
    }
}
