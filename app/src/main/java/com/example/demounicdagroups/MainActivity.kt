package com.example.demounicdagroups

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.demounicdagroups.core.theme.DemoUnicdaGroupsTheme
import com.example.demounicdagroups.features.auth.AuthViewModel
import com.example.demounicdagroups.features.auth.login.LoginViewModel
import com.example.demounicdagroups.navigation.BottomNavItem
import com.example.demounicdagroups.navigation.MyAppNavigation
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        val loginViewModel: LoginViewModel by viewModels()

        setContent {
            DemoUnicdaGroupsTheme {

                val authState by authViewModel.authState.observeAsState()

                LaunchedEffect(authState) {
                    if (authState is AuthState.Authenticated) {
                        loginViewModel.updateFcmToken()
                    }
                }

                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val bottomBarVisible = when (currentDestination?.route) {
                    "home", "chat", "notifications" -> true
                    else -> false
                }

                val items = listOf(
                    BottomNavItem(
                        title = "home",
                        selectedIcon = Icons.Filled.Home,
                        unSelectedIcon = Icons.Outlined.Home,
                        hasNews = false
                    ),
                    BottomNavItem(
                        title = "chat",
                        selectedIcon = Icons.Filled.Email,
                        unSelectedIcon = Icons.Outlined.Email,
                        hasNews = false,
                        badgeCount = 100
                    ),
                    BottomNavItem(
                        title = "notifications",
                        selectedIcon = Icons.Filled.Notifications,
                        unSelectedIcon = Icons.Outlined.Notifications,
                        hasNews = true
                    ),
                )

                var selectedItemIndex by remember {
                    mutableStateOf(0)
                }

                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (bottomBarVisible) {
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = {
                                            selectedItemIndex = index
                                            navController.navigate(item.title) {
                                                popUpTo(navController.graph.startDestinationId)
                                                launchSingleTop = true
                                            }
                                        },

                                        label = {
                                            item.title
                                        },

                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if (item.badgeCount != null) {
                                                        Badge {
                                                            Text(text = item.badgeCount.toString())
                                                        }
                                                    } else if (item.hasNews) {
                                                        Badge()
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (index == selectedItemIndex) {
                                                        item.selectedIcon
                                                    } else {
                                                        item.unSelectedIcon
                                                    },
                                                    contentDescription = item.title
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    ) { innerPadding ->
                    MyAppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

