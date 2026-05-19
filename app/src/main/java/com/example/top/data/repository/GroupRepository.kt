package com.example.top.data.repository

import com.example.top.data.model.GroupRole
import com.example.top.data.model.GroupSummary

interface GroupRepository {
    suspend fun getCreatedGroups(userId: String): Result<List<GroupSummary>>
    suspend fun getJoinedGroups(userId: String): Result<List<GroupSummary>>
}

class DemoGroupRepository : GroupRepository {
    override suspend fun getCreatedGroups(userId: String): Result<List<GroupSummary>> = Result.success(
        listOf(
            GroupSummary("1", "Class 8 Scoreboard", "Demo Admin", 32, GroupRole.ADMIN),
            GroupSummary("2", "Sunday Cricket Team", "Demo Admin", 18, GroupRole.ADMIN)
        )
    )

    override suspend fun getJoinedGroups(userId: String): Result<List<GroupSummary>> = Result.success(
        listOf(
            GroupSummary("3", "Math Champions", "Neha Teacher", 28, GroupRole.STUDENT)
        )
    )
}
