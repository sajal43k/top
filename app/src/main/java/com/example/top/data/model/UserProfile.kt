package com.example.top.data.model

data class UserProfile(
    val name: String = "",
    val phoneNumber: String = "",
    val profession: String = "",
    val age: String = "",
    val profileImageUri: String? = null,
    val firstPetName: String = "",
    val hasNoPet: Boolean = false,
    val firstSchoolName: String = "",
    val firstFriendName: String = ""
)
