package com.example.demounicdagroups.features.group

import androidx.lifecycle.ViewModel
import com.example.demounicdagroups.Data.GroupInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _groups = MutableStateFlow<List<GroupInfo>>(emptyList())
    val groups = _groups.asStateFlow()

    private val _joinedGroups = MutableStateFlow<List<GroupInfo>>(emptyList())
    val joinedGroups = _joinedGroups.asStateFlow()

    init {
        listenForAllGroups()
        listenForUserGroups()
    }

    private fun listenForAllGroups() {
        firestore.collection("study_groups")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    _groups.value = snapshot.toObjects(GroupInfo::class.java)
                }
            }
    }

    private fun listenForUserGroups() {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        if (uid == null) {
            _joinedGroups.value = emptyList()
            return
        }

        firestore.collection("study_groups")
            .whereArrayContains("members", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val myGroups = snapshot.toObjects(GroupInfo::class.java)
                    _joinedGroups.value = myGroups
                }
            }
    }

    fun joinGroup(groupId: String) {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid
        val groupRef = firestore.collection("study_groups").document(groupId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(groupRef)
            val members = snapshot.get("members") as? List<String> ?: emptyList()

            if (members.contains(uid)) {
                return@runTransaction
            }
            transaction.update(groupRef, "members", FieldValue.arrayUnion(uid))
            transaction.update(groupRef, "memberCount", FieldValue.increment(1))
        }.addOnSuccessListener {
            println("Transaction success!")

        }.addOnFailureListener { e ->
            println("Transaction failure: $e")
        }
    }

    fun createGroup(name: String, courseCode: String, groupDetail: String) {
        val currentUser = auth.currentUser ?: return

        val newGroup = hashMapOf(
            "name" to name,
            "courseCode" to courseCode,
            "groupDetail" to groupDetail,
            "memberCount" to 1,
            "members" to listOf(currentUser.uid),
            "creatorUid" to currentUser.uid,
            "creatorName" to currentUser.displayName,
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("study_groups").add(newGroup)
    }
}