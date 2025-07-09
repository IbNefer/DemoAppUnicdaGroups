package com.example.demounicdagroups.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.demounicdagroups.AuthState
import com.example.demounicdagroups.AuthViewModel

@Composable
fun HomePage(modifier: Modifier, navController: NavHostController, AuthViewModel: AuthViewModel){
    val authState = AuthViewModel.authState.observeAsState()

    LaunchedEffect (authState.value){
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Welcome to my page!", fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            AuthViewModel.signout()
        }) {
            Text(text = "Sign out...")
        }
    }
}