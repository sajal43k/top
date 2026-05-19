package com.example.top.data.repository

import com.example.top.data.model.UserProfile

/**
 * Firebase-ready authentication contract. Replace the in-memory implementation
 * with Firebase Auth + Firestore calls when the Firebase project is connected.
 */
interface AuthRepository {
    suspend fun login(identifier: String, password: String): Result<UserProfile>
    suspend fun createAccount(profile: UserProfile, password: String): Result<UserProfile>
    suspend fun recoverPassword(
        identifier: String,
        firstPetName: String,
        hasNoPet: Boolean,
        firstSchoolName: String,
        firstFriendName: String
    ): Result<Unit>
}

class InMemoryAuthRepository : AuthRepository {
    private val demoUser = UserProfile(
        name = "Demo Admin",
        phoneNumber = "9999999999",
        profession = "Teacher",
        age = "28",
        firstPetName = "Moti",
        firstSchoolName = "Central School",
        firstFriendName = "Aman"
    )

    override suspend fun login(identifier: String, password: String): Result<UserProfile> {
        return if (identifier.isNotBlank() && password.isNotBlank()) Result.success(demoUser)
        else Result.failure(IllegalArgumentException("Enter name/phone and password."))
    }

    override suspend fun createAccount(profile: UserProfile, password: String): Result<UserProfile> {
        return when {
            profile.name.isBlank() -> Result.failure(IllegalArgumentException("Name is required."))
            profile.phoneNumber.length < 10 -> Result.failure(IllegalArgumentException("Enter a valid phone number."))
            password.length < 6 -> Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
            else -> Result.success(profile)
        }
    }

    override suspend fun recoverPassword(
        identifier: String,
        firstPetName: String,
        hasNoPet: Boolean,
        firstSchoolName: String,
        firstFriendName: String
    ): Result<Unit> {
        return if (identifier.isNotBlank() && firstSchoolName.isNotBlank() && firstFriendName.isNotBlank()) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Fill the recovery questions."))
        }
    }
}
