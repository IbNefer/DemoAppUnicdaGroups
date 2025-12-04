package com.example.demounicdagroups.features.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ImageViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl = _profileImageUrl.asStateFlow()

    init {
        fetchProfileImage()
    }

    private fun fetchProfileImage() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val document = firestore.collection("users").document(uid).get().await()
                _profileImageUrl.value = document.getString("profileImageUrl")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun uploadImage(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return

        val fileLocation = storageRef.child("profile_images/$uid.jpg")

        viewModelScope.launch {
            try {
                fileLocation.putFile(uri).await()

                val downloadUrl = fileLocation.downloadUrl.await().toString()


                firestore.collection("users").document(uid)
                    .update("profileImageUrl", downloadUrl)
                    .await()

                _profileImageUrl.value = downloadUrl

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}