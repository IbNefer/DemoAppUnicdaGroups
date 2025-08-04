package com.example.demounicdagroups.features.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthViewModel(): ViewModel(){
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _events = Channel<AuthEvent>()
    val events = _events.receiveAsFlow()

    init{
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        val currentUser = auth.currentUser
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
        }
        else{
            _authState.value = AuthState.Authenticated(currentUser)
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email or password can't be empty.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Pass the user object after successful login
                    _authState.value = AuthState.Authenticated(auth.currentUser!!)
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong!")
                }
            }
    }

    fun signup(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields.")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        updateUserProfile(firebaseUser, name)
                    } else {
                        _authState.value = AuthState.Error("User not found after creation.")
                    }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong!")
                }
            }
    }
    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    private fun updateUserProfile(firebaseUser: FirebaseUser, name: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        firebaseUser.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 3. After updating the profile, save user details to Firestore
                    saveUserToFirestore(firebaseUser, name)
                } else {
                    _authState.value = AuthState.Error("Failed to update profile.")
                }
            }
    }



    private fun saveUserToFirestore(firebaseUser: FirebaseUser, name: String) {
        _authState.value = AuthState.Authenticated(firebaseUser)
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "uid" to firebaseUser.uid,
            "name" to name,
            "email" to firebaseUser.email,
            "role" to "user"
        )

        db.collection("users").document(firebaseUser.uid)
            .set(user)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _events.send(AuthEvent.ShowToast("Account created successfully!"))
                    _events.send(AuthEvent.Navigate("login"))
                }
            }
            .addOnFailureListener { e ->
                viewModelScope.launch {
                    _events.send(AuthEvent.ShowToast("Failed to save user data: ${e.message}"))
                }
            }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Unauthenticated
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}



sealed class AuthState{
    data class Authenticated(val user: FirebaseUser?) : AuthState()
    object Unauthenticated: AuthState()
    object Loading: AuthState()
    data class  Error(val message: String): AuthState()
}

sealed interface AuthEvent {
    data class Navigate(val route: String) : AuthEvent
    data class ShowToast(val message: String) : AuthEvent
}

