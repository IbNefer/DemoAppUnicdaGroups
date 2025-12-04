package com.example.demounicdagroups.features.auth.login

import AuthEvent
import AuthState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    private val _loginState = MutableLiveData<AuthState>()
    val loginState: LiveData<AuthState> = _loginState

    private val _events = Channel<AuthEvent>()
    val events = _events.receiveAsFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("Email or password can't be empty.")
            return
        }

        _loginState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _loginState.value = AuthState.Authenticated(user)

                    updateFcmToken()

                    triggerSuccessEvents()
                } else {
                    _loginState.value = AuthState.Error(task.exception?.message ?: "Login failed")
                }
            }
    }

    // --- LOGIN CON GOOGLE ---
    fun loginWithGoogle(credential: AuthCredential) {
        _loginState.value = AuthState.Loading

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        checkIfUserExistsInFirestore(user)
                    }
                } else {
                    _loginState.value = AuthState.Error(task.exception?.message ?: "Google Login failed")
                }
            }
    }

    private fun checkIfUserExistsInFirestore(user: FirebaseUser) {
        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Usuario ya registrado: Entrar directo
                    _loginState.value = AuthState.Authenticated(user)
                    updateFcmToken()
                    triggerSuccessEvents()
                } else {
                    // Usuario nuevo: Crear perfil
                    val name = user.displayName ?: "Usuario Google"
                    saveGoogleUserToFirestore(user, name)
                }
            }
            .addOnFailureListener {
                _loginState.value = AuthState.Error("Error connecting to database")
            }
    }

    private fun saveGoogleUserToFirestore(user: FirebaseUser, name: String) {
        val userData = hashMapOf(
            "uid" to user.uid,
            "name" to name,
            "email" to (user.email ?: ""),
            "role" to "user"
        )

        firestore.collection("users").document(user.uid).set(userData)
            .addOnSuccessListener {
                _loginState.value = AuthState.Authenticated(user)
                updateFcmToken()
                triggerSuccessEvents()
            }
            .addOnFailureListener {
                _loginState.value = AuthState.Error("Failed to create profile")
            }
    }

    // --- FUNCIONES DE AYUDA ---

    private fun triggerSuccessEvents() {
        viewModelScope.launch {
            _events.send(AuthEvent.ShowToast("Login Successful!"))
            _events.send(AuthEvent.Navigate("home"))
        }
    }

    fun updateFcmToken() {
        val uid = auth.currentUser?.uid ?: return

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                firestore.collection("users").document(uid)
                    .update("fcmToken", token)
            }
        }
    }
}