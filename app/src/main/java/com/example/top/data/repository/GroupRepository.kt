package com.example.top.data.repository

import com.example.top.data.model.GroupSummary

interface GroupRepository {
    suspend fun getCreatedGroups(userId: String): Result<List<GroupSummary>>
    suspend fun getJoinedGroups(userId: String): Result<List<GroupSummary>>
}

class DemoGroupRepository : GroupRepository {
    override suspend fun getCreatedGroups(userId: String): Result<List<GroupSummary>> = Result.success(emptyList())

    override suspend fun getJoinedGroups(userId: String): Result<List<GroupSummary>> = Result.success(emptyList())
}
