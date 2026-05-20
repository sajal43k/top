package com.example.top.data.model

data class GroupSummary(
    val id: String,
    val name: String,
    val description: String = "",
    val ownerName: String,
    val memberCount: Int,
    val role: GroupRole
)

enum class GroupRole { ADMIN, STUDENT }
