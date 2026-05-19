package com.example.top.firebase

/**
 * Firebase hook point for the next milestone.
 *
 * After adding google-services.json and Firebase dependencies, create concrete
 * repositories here that talk to Firebase Auth, Firestore, and Storage.
 */
object FirebaseConfig {
    const val USERS_COLLECTION = "users"
    const val GROUPS_COLLECTION = "groups"
    const val SCORES_COLLECTION = "scores"
    const val ATTENDANCE_COLLECTION = "attendance"
}
