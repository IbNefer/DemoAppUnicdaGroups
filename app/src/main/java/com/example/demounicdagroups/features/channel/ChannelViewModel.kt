package com.example.demounicdagroups.features.channel

import androidx.lifecycle.ViewModel
import com.example.demounicdagroups.Data.GroupInfo // Usamos tu modelo de Grupos (porque los canales SON grupos)
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _channels = MutableStateFlow<List<GroupInfo>>(emptyList())
    val channels = _channels.asStateFlow()

    init {
        getUserChannels()
    }

    private fun getUserChannels() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            _channels.value = emptyList()
            return
        }

        firestore.collection("study_groups")
            .whereArrayContains("members", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {

                }

                if (snapshot != null) {
                    val list = snapshot.toObjects(GroupInfo::class.java)
                    _channels.value = list
                }
            }
    }
}