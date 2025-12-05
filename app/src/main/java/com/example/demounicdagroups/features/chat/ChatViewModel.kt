package com.example.demounicdagroups.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demounicdagroups.Data.Message
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(): ViewModel() {
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val message = _messages.asStateFlow()
    fun sendMessage(channelId: String, messageText: String) {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users").document(uid).get().await()
                val userName = userDoc.getString("name") ?: "Usuario"
                val userPhotoUrl = userDoc.getString("profileImageUrl")

                val newMessage = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = uid,
                    senderName = userName,
                    message = messageText,
                    createdAt = System.currentTimeMillis(),
                    senderProfileUrl = userPhotoUrl
                )

                firestore.collection("groups").document(channelId)
                    .collection("messages")
                    .add(newMessage)
                    .await()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun listenForMessages(channelId: String) {
        firestore.collection("groups").document(channelId)
            .collection("messages")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->


                if (error != null) {
                    return@addSnapshotListener
                }


                if (snapshot != null) {
                    val list = snapshot.toObjects(Message::class.java)
                    _messages.value = list
                }
            }
    }

    private val storage = Firebase.storage

    fun sendMediaMessage(channelId: String, uri: android.net.Uri, caption: String = "") {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users").document(uid).get().await()
                val userName = userDoc.getString("name") ?: "Usuario"
                val userPhotoUrl = userDoc.getString("profileImageUrl")

                val imageRef = storage.reference.child("chat_media/${UUID.randomUUID()}")

                imageRef.putFile(uri).await()

                val downloadUrl = imageRef.downloadUrl.await().toString()

                val newMessage = Message(
                    id = UUID.randomUUID().toString(),
                    senderId = uid,
                    senderName = userName,
                    message = caption, // Texto opcional
                    createdAt = System.currentTimeMillis(),
                    senderProfileUrl = userPhotoUrl,
                    imageUrl = downloadUrl
                )

                firestore.collection("groups").document(channelId)
                    .collection("messages")
                    .add(newMessage)
                    .await()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
