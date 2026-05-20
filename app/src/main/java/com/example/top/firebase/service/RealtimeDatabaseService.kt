package com.example.top.firebase.service

import com.example.top.data.model.GroupRole
import com.example.top.data.model.GroupSummary
import com.example.top.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RealtimeDatabaseService(
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val usersRef = db.getReference("users")
    private val groupsRef = db.getReference("groups")
    private val userGroupsRef = db.getReference("user_groups")
    private val joinedGroupsRef = db.getReference("joined_groups")

    suspend fun saveUser(uid: String, profile: UserProfile, email: String) {
        val payload = mapOf(
            "uid" to uid,
            "name" to profile.name,
            "phoneNumber" to profile.phoneNumber,
            "profession" to profile.profession,
            "age" to profile.age,
            "profileImageUri" to (profile.profileImageUri ?: ""),
            "firstPetName" to profile.firstPetName,
            "hasNoPet" to profile.hasNoPet,
            "firstSchoolName" to profile.firstSchoolName,
            "firstFriendName" to profile.firstFriendName,
            "email" to email
        )
        usersRef.child(uid).setValue(payload).await()
    }

    suspend fun getUser(uid: String): UserProfile {
        val snapshot = usersRef.child(uid).get().await()
        return snapshot.toUserProfile()
    }

    suspend fun updateUser(uid: String, profile: UserProfile) {
        val updates = mapOf(
            "name" to profile.name,
            "phoneNumber" to profile.phoneNumber,
            "profession" to profile.profession,
            "age" to profile.age,
            "firstPetName" to profile.firstPetName,
            "hasNoPet" to profile.hasNoPet,
            "firstSchoolName" to profile.firstSchoolName,
            "firstFriendName" to profile.firstFriendName
        )
        usersRef.child(uid).updateChildren(updates).await()
    }

    suspend fun createGroup(ownerUid: String, ownerName: String, groupName: String, description: String) {
        val groupId = groupsRef.push().key ?: error("Failed to generate group ID")
        val groupPayload = mapOf(
            "id" to groupId,
            "name" to groupName,
            "description" to description,
            "ownerUid" to ownerUid,
            "ownerName" to ownerName,
            "memberCount" to 1
        )
        groupsRef.child(groupId).setValue(groupPayload).await()
        userGroupsRef.child(ownerUid).child(groupId).setValue(true).await()
        joinedGroupsRef.child(ownerUid).child(groupId).setValue(true).await()
    }

    suspend fun joinGroup(userUid: String, groupId: String) {
        val joinRef = joinedGroupsRef.child(userUid).child(groupId)
        if (joinRef.get().await().exists()) return

        val ownerMap = userGroupsRef.child(userUid).child(groupId).get().await().exists()
        if (!ownerMap) {
            joinRef.setValue(true).await()
            val memberCountRef = groupsRef.child(groupId).child("memberCount")
            val current = memberCountRef.get().await().getValue(Int::class.java) ?: 0
            memberCountRef.setValue(current + 1).await()
        }
    }

    fun observeCreatedGroups(userUid: String): Flow<List<GroupSummary>> = callbackFlow {
        val ref = userGroupsRef.child(userUid)
        val listener = ref.listenGroupList(role = GroupRole.ADMIN, onUpdate = { trySend(it) })
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeJoinedGroups(userUid: String): Flow<List<GroupSummary>> = callbackFlow {
        val ref = joinedGroupsRef.child(userUid)
        val listener = ref.listenGroupList(role = GroupRole.STUDENT, onUpdate = { trySend(it) })
        awaitClose { ref.removeEventListener(listener) }
    }

    private fun com.google.firebase.database.DatabaseReference.listenGroupList(
        role: GroupRole,
        onUpdate: (List<GroupSummary>) -> Unit
    ): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = snapshot.children.mapNotNull { it.key }
                if (ids.isEmpty()) {
                    onUpdate(emptyList())
                    return
                }
                groupsRef.get().addOnSuccessListener { groupsSnap ->
                    val groups = ids.mapNotNull { id ->
                        val node = groupsSnap.child(id)
                        if (!node.exists()) null else GroupSummary(
                            id = id,
                            name = node.child("name").getValue(String::class.java).orEmpty(),
                            description = node.child("description").getValue(String::class.java).orEmpty(),
                            ownerName = node.child("ownerName").getValue(String::class.java).orEmpty(),
                            memberCount = node.child("memberCount").getValue(Int::class.java) ?: 0,
                            role = role
                        )
                    }
                    onUpdate(groups)
                }.addOnFailureListener { onUpdate(emptyList()) }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) = Unit
        }
        addValueEventListener(listener)
        return listener
    }

    fun currentUserId(): String? = auth.currentUser?.uid

    private fun DataSnapshot.toUserProfile(): UserProfile = UserProfile(
        uid = child("uid").getValue(String::class.java).orEmpty(),
        name = child("name").getValue(String::class.java).orEmpty(),
        phoneNumber = child("phoneNumber").getValue(String::class.java).orEmpty(),
        profession = child("profession").getValue(String::class.java).orEmpty(),
        age = child("age").getValue(String::class.java).orEmpty(),
        profileImageUri = child("profileImageUri").getValue(String::class.java),
        firstPetName = child("firstPetName").getValue(String::class.java).orEmpty(),
        hasNoPet = child("hasNoPet").getValue(Boolean::class.java) ?: false,
        firstSchoolName = child("firstSchoolName").getValue(String::class.java).orEmpty(),
        firstFriendName = child("firstFriendName").getValue(String::class.java).orEmpty()
    )
}
