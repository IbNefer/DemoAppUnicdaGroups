package com.example.demounicdagroups.features.group

import androidx.lifecycle.ViewModel
import com.example.demounicdagroups.features.group.GroupInfo
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


): ViewModel() {

    private val _groups = MutableStateFlow<List<GroupInfo>>(emptyList())
    val groups = _groups.asStateFlow()

    init{
        listenForGroupChanges()
    }

    private fun listenForGroupChanges(){
        firestore.collection("study_groups")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null){
                    return@addSnapshotListener
                }
                if (snapshot != null){
                    val groupList = snapshot.toObjects(GroupInfo::class.java)
                    _groups.value = groupList
                }
            }
    }

    fun joinGroup(groupId: String){
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

    fun createGroup(name: String, courseCode: String, groupDetail: String){
        val currentUser = auth.currentUser ?: return

        val newCroup = hashMapOf(
            "name" to name,
            "courseCode" to courseCode,
            "groupDetail" to groupDetail,
            "memberCount" to 1,
            "creatorUid" to currentUser.uid,
            "creatorName" to currentUser.displayName,
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("study_groups").add(newCroup)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }
}