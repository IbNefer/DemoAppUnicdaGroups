package com.example.demounicdagroups.features.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.demounicdagroups.core.components.SearchBar
import com.example.demounicdagroups.core.components.ClickableCard
import com.example.demounicdagroups.features.group.GroupDetailsDialog
import com.example.demounicdagroups.Data.GroupInfo
import com.example.demounicdagroups.features.group.GroupViewModel
import com.example.demounicdagroups.core.theme.Blue1
import com.example.demounicdagroups.features.auth.AuthViewModel
import com.example.demounicdagroups.features.notification.RequestNotificationPermissionDialog

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    groupsViewModel: GroupViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    RequestNotificationPermissionDialog()
    val authState by authViewModel.authState.observeAsState()
    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }


    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    var selectedGroup by remember { mutableStateOf<GroupInfo?>(null) }
    val studyGroups by groupsViewModel.groups.collectAsState()

    val filteredGroups = remember(searchText, studyGroups) {
        if (searchText.isBlank()) {
            studyGroups
        } else {
            studyGroups.filter { group ->
                group.name.contains(searchText, ignoreCase = true)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currentUserUid = authViewModel.getCurrentUser()?.uid

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Study Groups",
                        color = Blue1,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Icon(
                        imageVector = Icons.Outlined.Add,
                        tint = Color.Black,
                        contentDescription = "Icono añadir",
                        modifier = Modifier.align(Alignment.CenterStart)
                            .clickable{
                                navController.navigate("createGroup")
                            }
                    )

                    Icon(
                        imageVector = Icons.Outlined.Person,
                        tint = Color.Blue,
                        contentDescription = "Ícono de Usuario",
                        modifier = Modifier.align(Alignment.CenterEnd)
                            .clickable{
                                navController.navigate("profile")
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SearchBar(
                        searchText = searchText,
                        onSearchTextChange = { newText -> searchText = newText }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            items(filteredGroups) { group ->
                val isJoined = group.members.contains(currentUserUid)

                ClickableCard(
                    groupInfo = group,
                    isJoined = isJoined,
                    onClick = { selectedGroup = group },
                    onButtonClick = {
                        groupsViewModel.joinGroup(group.id)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextButton(onClick = {
                    authViewModel.signout()
                }) {
                    Text(text = "Sign out...")
                }
            }
        }

        selectedGroup?.let { group ->
            GroupDetailsDialog(
                title = group.name,
                details = group.groupDetail,
                onDismiss = { selectedGroup = null }
            )
        }
    }
}
