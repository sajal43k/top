package com.example.top.data.repository

import android.net.Uri
import com.example.top.data.model.UserProfile
import com.example.top.firebase.service.FirebaseAuthService
import com.example.top.firebase.service.FirebaseStorageService
import com.example.top.firebase.service.FirestoreService

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
    fun hasSession(): Boolean
    fun logout()
}

class FirebaseAuthRepository(
    private val authService: FirebaseAuthService = FirebaseAuthService(),
    private val firestoreService: FirestoreService = FirestoreService(),
    private val storageService: FirebaseStorageService = FirebaseStorageService()
) : AuthRepository {

    override suspend fun login(identifier: String, password: String): Result<UserProfile> = runCatching {
        val firebaseUser = authService.login(identifier.trim(), password)
        firestoreService.getUser(firebaseUser.uid)
    }

    override suspend fun createAccount(
        profile: UserProfile,
        email: String,
        password: String,
        profileImageUri: Uri?
    ): Result<UserProfile> = runCatching {
        val firebaseUser = authService.createUser(email.trim(), password)
        val imageUrl = profileImageUri?.let { storageService.uploadProfileImage(firebaseUser.uid, it) }
        val updatedProfile = profile.copy(profileImageUri = imageUrl)
        firestoreService.saveUser(firebaseUser.uid, updatedProfile, email)
        updatedProfile
    }

    override suspend fun getLoggedInUser(): Result<UserProfile> = runCatching {
        val user = authService.currentUser() ?: error("No active session")
        firestoreService.getUser(user.uid)
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        authService.sendPasswordReset(email)
    }

    override fun hasSession(): Boolean = authService.currentUser() != null

    override fun logout() = authService.logout()
}
