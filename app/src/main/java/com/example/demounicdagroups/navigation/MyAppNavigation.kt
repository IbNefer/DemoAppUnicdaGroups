package com.example.demounicdagroups.navigation

import SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.demounicdagroups.features.auth.AuthViewModel
import com.example.demounicdagroups.features.group.CreateGroup
import com.example.demounicdagroups.features.home.HomePage
import com.example.demounicdagroups.features.auth.login.LoginPage
import com.example.demounicdagroups.features.notification.NotificationPage
import com.example.demounicdagroups.features.auth.signup.SignupPage
import com.example.demounicdagroups.features.channel.ChannelPage
import com.example.demounicdagroups.features.chat.ChatPage
import com.example.demounicdagroups.features.profile.ProfileUser


data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)



@Composable
fun MyAppNavigation(
    modifier: Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    NavHost(navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable(route = "login") {
            LoginPage(modifier, navController)
        }
        composable(route = "signup") {
            SignupPage(modifier, navController)
        }
        composable(route = "home") {
            HomePage(modifier, navController)
        }
        composable(route = "chat") {
            ChannelPage(modifier, navController)
        }
        composable (route = "notifications"){
            NotificationPage(modifier)
        }
        composable (route= "createGroup"){
            CreateGroup(navController = navController)
        }
        composable (route= "profile"){
            ProfileUser(navController = navController)
        }
        composable("chat/{channelId}", arguments = listOf(
            navArgument("channelId"){
                type = NavType.StringType
            }
        ) ) {
            val channelId = it.arguments?.getString("channelId") ?: " "
            ChatPage(navController, channelId)
        }
    }
}
