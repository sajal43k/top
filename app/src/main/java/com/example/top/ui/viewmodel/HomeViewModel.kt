package com.example.top.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.top.data.model.GroupRole
import com.example.top.data.model.GroupSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val userName: String = "Demo Admin",
    val searchQuery: String = "",
    val createdGroups: List<GroupSummary> = listOf(
        GroupSummary("1", "Class 8 Scoreboard", "Demo Admin", 32, GroupRole.ADMIN),
        GroupSummary("2", "Sunday Cricket Team", "Demo Admin", 18, GroupRole.ADMIN)
    ),
    val joinedGroups: List<GroupSummary> = listOf(
        GroupSummary("3", "Math Champions", "Neha Teacher", 28, GroupRole.STUDENT)
    ),
    val isInternetAvailable: Boolean = true
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
