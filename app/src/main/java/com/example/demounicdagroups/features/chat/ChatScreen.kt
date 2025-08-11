package com.example.demounicdagroups.features.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import com.example.demounicdagroups.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.demounicdagroups.features.group.GroupViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, channelId: String) {
    val chatViewModel: ChatViewModel = hiltViewModel()
    val groupsViewModel: GroupViewModel = hiltViewModel()

    val allGroups by groupsViewModel.groups.collectAsState()

    val groupInfo = remember(allGroups, channelId) {
        allGroups.find { it.id == channelId }
    }

    LaunchedEffect(key1 = channelId) {
        chatViewModel.listenForMessages(channelId)
    }

    val messages by chatViewModel.message.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(groupInfo?.name ?: "Chat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        ChatMessages(
            modifier = Modifier.padding(paddingValues),
            messages = messages,
            onSendMessage = { message ->
                chatViewModel.sendMessage(channelId, message)
            }
        )
    }
}
@Composable
fun ChannelItem(channelName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = channelName.firstOrNull()?.uppercase() ?: "",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = channelName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
@Composable
fun ChatMessages(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }
        }

        // The message input field at the bottom.
        MessageInput(onSendMessage = onSendMessage)
    }
}

@Composable
fun MessageInput(onSendMessage: (String) -> Unit) {
    var message by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp // Adds a nice shadow above the input field.
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                shape = RoundedCornerShape(24.dp),
                keyboardActions = KeyboardActions(onSend = {
                    if (message.isNotBlank()) {
                        onSendMessage(message)
                        message = ""
                        keyboardController?.hide()
                    }
                }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        onSendMessage(message)
                        message = ""
                    }
                },
                enabled = message.isNotBlank()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.primary
                )
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