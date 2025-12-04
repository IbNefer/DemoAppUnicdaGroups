package com.example.demounicdagroups.features.auth.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.demounicdagroups.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by loginViewModel.loginState.observeAsState()
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
                loginViewModel.loginWithGoogle(firebaseCredential)
            }
        }
    }

    LaunchedEffect(key1 = true) {
        loginViewModel.events.collect { event ->
            when (event) {
                is AuthEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is AuthEvent.Navigate -> {
                    navController.navigate(event.route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
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
            text = "Login",
            fontSize = 32.sp,
            color = colorResource(id = R.color.dark_blue)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            textStyle = TextStyle(color = Color.Black),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            textStyle = TextStyle(color = Color.Black),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                loginViewModel.login(email, password)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.dark_blue),
                contentColor = Color.White
            ),
            enabled = loginState != AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loginState == AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text(text = "Login")
            }
        }

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
            enabled = loginState != AuthState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login with Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            navController.navigate("signup")
        }) {
            Text(text = "Don't have an account? Signup")
        }
    }
}
