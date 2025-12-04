package com.example.demounicdagroups.features.auth.signup

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.demounicdagroups.R
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun SignupPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    signupViewModel: SignupViewModel = hiltViewModel()
) {
    // Variables de estado para los campos de texto
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val signupState by signupViewModel.signupState.observeAsState()
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val oneTapClient = remember { Identity.getSignInClient(context) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val googleIdToken = credential.googleIdToken
            if (googleIdToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                signupViewModel.signupWithGoogle(firebaseCredential)
            }
        }
    }

    LaunchedEffect(key1 = true) {
        signupViewModel.events.collect { event ->
            when (event) {
                is AuthEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is AuthEvent.Navigate -> navController.navigate(event.route) { popUpTo("signup") { inclusive = true } }
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.unicda),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )

        Text(
            text = "Sign Up",
            fontSize = 32.sp,
            color = colorResource(id = R.color.dark_blue)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- ¡AQUÍ ESTABAN LOS CAMPOS FALTANTES! ---
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
            // visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { signupViewModel.signup(name, email, password) },
            enabled = signupState != AuthState.Loading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.dark_blue),
                contentColor = Color.White
            )
        ) {
            if (signupState == AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text(text = "Create an account")
            }
        }

        // ... (El resto de tus botones de Google y Login se quedan igual) ...
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "OR", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    try {
                        val signInRequest = BeginSignInRequest.builder()
                            .setGoogleIdTokenRequestOptions(
                                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                    .setSupported(true)
                                    .setServerClientId(context.getString(R.string.default_web_client_id))
                                    .setFilterByAuthorizedAccounts(false)
                                    .build()
                            )
                            .build()

                        val result = oneTapClient.beginSignIn(signInRequest).await()
                        googleLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error iniciando Google: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = signupState != AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign up with Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text(text = "Already have an account, Log in!")
        }
    }
}


