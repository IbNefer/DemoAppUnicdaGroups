package com.example.demounicdagroups.features.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.example.demounicdagroups.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.demounicdagroups.core.theme.Blueberry
import com.example.demounicdagroups.core.theme.Blueish
import com.example.demounicdagroups.core.theme.LightGreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ChatScreen(navController: NavController, channelId: String){

    Scaffold(
        containerColor = Color.LightGray
    ){
        Column (
            modifier = Modifier.fillMaxSize().padding(it)
        ){
            val viewModel: ChatViewModel = hiltViewModel()
            LaunchedEffect(key1 = true) {
                viewModel.listenForMessages(channelId)
            }

            val messages = viewModel.message.collectAsState()

            ChatMessages(
                messages = messages.value,
                onSendMessage = {message ->
                    viewModel.sendMessage(channelId, message)
                }
            )
        }
    }
}

@Composable
fun ChannelItem(channelName: String, onClick: ()-> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Blueish)
            .clickable{
            onClick()
        },
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.Yellow.copy(alpha = 0.3f))
        ){
            Text(
                text = channelName[0].uppercase(),
                color = Color.White,
                style = TextStyle(fontSize = 35.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center))
        }

        Text(
            text = channelName,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Composable
fun ChatMessages(
  messages: List<Message>,
  onSendMessage: (String) -> Unit,
){
    val hideKeyboardController = LocalSoftwareKeyboardController.current

    val msg = remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        LazyColumn {
            items(messages){message ->
                ChatBubble(message = message)
            }
        }

        Row (
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ){
            TextField(
                value = msg.value,
                onValueChange = {
                    msg.value= it
                },
                modifier = Modifier.weight(1f),
                placeholder = {Text(text = "Type a message")} ,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        hideKeyboardController?.hide()
                    }
                )
            )

            IconButton(onClick = {
                onSendMessage(msg.value)
                msg.value = ""
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid

    // Define colors from your app's theme for a consistent look
    val bubbleColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = if (isCurrentUser) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    val horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.Top
    ) {
        if (!isCurrentUser) {
            ProfileImage(modifier = Modifier.padding(end = 8.dp))
        }

        Column {
            if (!isCurrentUser) {
                Text(
                    text = message.senderName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
                )
            }

            // The message bubble itself
            Text(
                text = message.message.trim(),
                color = textColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(bubbleColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    // Constrain the width so long messages wrap nicely
                    .widthIn(max = 280.dp)
            )
        }

        // For outgoing messages, show the picture on the right
        if (isCurrentUser) {
            ProfileImage(modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun ProfileImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.gatito),
        contentDescription = "Profile picture",
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
    )
}