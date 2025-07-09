package com.example.demounicdagroups

import SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demounicdagroups.pages.HomePage
import com.example.demounicdagroups.pages.LoginPage
import com.example.demounicdagroups.pages.SignupPage

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable(route = "login") {
            LoginPage(modifier, navController, authViewModel)
        }
        composable(route = "signup") {
            SignupPage(modifier, navController, authViewModel)
        }
        composable(route = "home") {
            HomePage(modifier, navController, authViewModel)
        }
    }
}