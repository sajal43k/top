package com.example.top.firebase.service

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageService(private val storage: FirebaseStorage = FirebaseStorage.getInstance()) {
    suspend fun uploadProfileImage(uid: String, uri: Uri): String {
        val ref = storage.reference.child("profile_images/$uid.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
