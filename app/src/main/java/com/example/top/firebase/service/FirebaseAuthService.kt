package com.example.top.firebase.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {
    fun currentUser(): FirebaseUser? = auth.currentUser

    suspend fun createUser(email: String, password: String): FirebaseUser {
        return auth.createUserWithEmailAndPassword(email, password).await().user
            ?: error("Failed to create user")
    }

    suspend fun login(email: String, password: String): FirebaseUser {
        return auth.signInWithEmailAndPassword(email, password).await().user
            ?: error("Failed to login")
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() = auth.signOut()
}
