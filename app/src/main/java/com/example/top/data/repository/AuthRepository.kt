package com.example.top.data.repository

import android.net.Uri
import com.example.top.data.model.UserProfile
import com.example.top.firebase.service.FirebaseAuthService
import com.example.top.firebase.service.RealtimeDatabaseService

interface AuthRepository {
    suspend fun login(identifier: String, password: String): Result<UserProfile>
    suspend fun createAccount(
        profile: UserProfile,
        email: String,
        password: String,
        profileImageUri: Uri? = null
    ): Result<UserProfile>
    suspend fun getLoggedInUser(): Result<UserProfile>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun updateProfile(profile: UserProfile): Result<UserProfile>
    fun hasSession(): Boolean
    fun logout()
}

class FirebaseAuthRepository(
    private val authService: FirebaseAuthService = FirebaseAuthService(),
    private val dbService: RealtimeDatabaseService = RealtimeDatabaseService()
) : AuthRepository {

    override suspend fun login(identifier: String, password: String): Result<UserProfile> = runCatching {
        val firebaseUser = authService.login(identifier.trim(), password)
        dbService.getUser(firebaseUser.uid)
    }

    override suspend fun createAccount(
        profile: UserProfile,
        email: String,
        password: String,
        profileImageUri: Uri?
    ): Result<UserProfile> = runCatching {
        val firebaseUser = authService.createUser(email.trim(), password)
        val updatedProfile = profile.copy(uid = firebaseUser.uid, profileImageUri = "")
        dbService.saveUser(firebaseUser.uid, updatedProfile, email)
        updatedProfile
    }

    override suspend fun getLoggedInUser(): Result<UserProfile> = runCatching {
        val user = authService.currentUser() ?: error("No active session")
        dbService.getUser(user.uid)
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        authService.sendPasswordReset(email)
    }

    override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> = runCatching {
        val uid = authService.currentUser()?.uid ?: error("No active session")
        dbService.updateUser(uid, profile)
        profile.copy(uid = uid)
    }

    override fun hasSession(): Boolean = authService.currentUser() != null

    override fun logout() = authService.logout()
}
