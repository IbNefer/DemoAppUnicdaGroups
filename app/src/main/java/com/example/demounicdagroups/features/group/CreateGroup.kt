package com.example.demounicdagroups.features.group

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.demounicdagroups.features.chat.ChannelViewModel

@Composable
fun CreateGroup(navController: NavController, groupsViewModel: GroupViewModel = hiltViewModel(), chatViewModel: ChannelViewModel = hiltViewModel()){
    var name by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var details by remember {mutableStateOf("")}
    val addChannel = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create New Group", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Group Name") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = courseCode, onValueChange = { courseCode = it }, label = { Text("Course Code / Aula") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = details, onValueChange = { details = it }, label = { Text("Details") })
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            groupsViewModel.createGroup(name, courseCode, details)
            chatViewModel.addChannel(courseCode)
            navController.popBackStack()
        }) {
            Text("Create Group")
        }
    }
}

