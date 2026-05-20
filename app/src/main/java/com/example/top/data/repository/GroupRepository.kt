package com.example.top.data.repository

import com.example.top.data.model.GroupSummary
import com.example.top.firebase.service.RealtimeDatabaseService
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun observeCreatedGroups(userId: String): Flow<List<GroupSummary>>
    fun observeJoinedGroups(userId: String): Flow<List<GroupSummary>>
    suspend fun createGroup(userId: String, ownerName: String, groupName: String, description: String): Result<Unit>
    suspend fun joinGroup(userId: String, groupId: String): Result<Unit>
}

class FirebaseGroupRepository(
    private val dbService: RealtimeDatabaseService = RealtimeDatabaseService()
) : GroupRepository {
    override fun observeCreatedGroups(userId: String): Flow<List<GroupSummary>> = dbService.observeCreatedGroups(userId)

    override fun observeJoinedGroups(userId: String): Flow<List<GroupSummary>> = dbService.observeJoinedGroups(userId)

    override suspend fun createGroup(userId: String, ownerName: String, groupName: String, description: String): Result<Unit> = runCatching {
        dbService.createGroup(userId, ownerName, groupName, description)
    }

    override suspend fun joinGroup(userId: String, groupId: String): Result<Unit> = runCatching {
        dbService.joinGroup(userId, groupId)
    }
}
