package com.example.demounicdagroups.features.channel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.demounicdagroups.features.chat.ChannelItem
import com.example.demounicdagroups.features.group.GroupViewModel

@Composable
fun ChannelPage(modifier: Modifier = Modifier, navController: NavController) {
    val viewModel = hiltViewModel<GroupViewModel>()

    val myChannels by viewModel.joinedGroups.collectAsState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ){
            if (myChannels.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No te has unido a ningún grupo aún.", color = Color.Gray)
                }
            }

            LazyColumn {
                item {
                    Text(
                        text = "Messages",
                        color = Color.Gray,
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Black),
                        modifier = Modifier.padding(16.dp)
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                }

                items(myChannels) { group ->
                    Column {
                        ChannelItem(
                            channelName = group.name,
                            onClick = {
                                navController.navigate("chat/${group.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}