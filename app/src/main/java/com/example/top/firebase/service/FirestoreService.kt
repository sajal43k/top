package com.example.top.firebase.service

import com.example.top.data.model.UserProfile
import com.example.top.firebase.FirebaseConfig
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreService(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    suspend fun saveUser(uid: String, profile: UserProfile, email: String) {
        val payload = hashMapOf(
            "uid" to uid,
            "name" to profile.name,
            "phoneNumber" to profile.phoneNumber,
            "profession" to profile.profession,
            "age" to profile.age,
            "profileImageUri" to profile.profileImageUri,
            "firstPetName" to profile.firstPetName,
            "hasNoPet" to profile.hasNoPet,
            "firstSchoolName" to profile.firstSchoolName,
            "firstFriendName" to profile.firstFriendName,
            "email" to email
        )
        db.collection(FirebaseConfig.USERS_COLLECTION).document(uid).set(payload).await()
    }

    suspend fun getUser(uid: String): UserProfile {
        val doc = db.collection(FirebaseConfig.USERS_COLLECTION).document(uid).get().await()
        return UserProfile(
            name = doc.getString("name").orEmpty(),
            phoneNumber = doc.getString("phoneNumber").orEmpty(),
            profession = doc.getString("profession").orEmpty(),
            age = doc.getString("age").orEmpty(),
            profileImageUri = doc.getString("profileImageUri"),
            firstPetName = doc.getString("firstPetName").orEmpty(),
            hasNoPet = doc.getBoolean("hasNoPet") ?: false,
            firstSchoolName = doc.getString("firstSchoolName").orEmpty(),
            firstFriendName = doc.getString("firstFriendName").orEmpty()
        )
    }
}
