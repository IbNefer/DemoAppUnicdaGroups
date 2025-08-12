package com.example.demounicdagroups.features.profile


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.demounicdagroups.features.auth.signup.AuthViewModel

@Composable
fun ProfileUser(navController: NavController){
    val authViewModel: AuthViewModel = hiltViewModel()
    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ){
                TextButton(onClick = {
                    authViewModel.signout()
                }) {
                    Text(text = "Sign out...")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun prevProfile(){
    ProfileUser(navController = rememberNavController())
}