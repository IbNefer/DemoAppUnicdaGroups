package com.example.demounicdagroups.features.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.demounicdagroups.features.chat.ChannelItem

@Composable
fun ChatPage(modifier: Modifier, navController: NavController) {
    val viewModel = hiltViewModel<ChannelViewModel>()
    val channels = viewModel.channels.collectAsState()

    Scaffold {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ){
            LazyColumn {
                item {
                    Text(text = "Messages",
                        color = Color.Gray,
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Black),
                        modifier = Modifier.padding(16.dp))
                    Spacer(modifier = Modifier.size(16.dp))
                }
                

                items(channels.value){channel ->
                    Column{
                        ChannelItem(channel.name){
                            navController.navigate("chat/${channel.id}")
                        }
                    }
                }
            }
        }
    }
}