package com.example.demounicdagroups.features.auth.signup

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.demounicdagroups.R


@Composable
fun SignupPage(modifier: Modifier, navController: NavHostController, AuthViewModel: AuthViewModel) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember{
        mutableStateOf("")
    }

    var name by remember {
        mutableStateOf("")
    }

    val authState = AuthViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated ->{
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                navController.navigate("login")
                AuthViewModel.resetAuthState()
            }
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }


    Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Image(
            painter = painterResource(id = R.drawable.unicda),
            contentDescription = "algo",
            modifier = Modifier.size(150.dp)
        )
            Text(text = "SignUp Page",
                fontSize = 32.sp,
                color = colorResource(id = R.color.dark_blue)
                )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text(text = "Email")
                },
                textStyle = TextStyle()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Full Name") }
            )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    Text(text = "Password")
                },
                textStyle = TextStyle()
            )


            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                AuthViewModel.signup(name, email, password)
            },
                enabled = authState.value != AuthState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue),
                contentColor = Color.White
            )
            ){
                Text(
                    text = "Create an account"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text(text = "Already have an account, Log in!")
            }
        }
    }




