package com.example.demounicdagroups.core.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.demounicdagroups.features.group.GroupInfo

@Composable
fun ClickableCard(
    modifier: Modifier = Modifier,
    groupInfo: GroupInfo,
    isJoined: Boolean, // New parameter to check if the user is a member
    onClick: () -> Unit,
    onButtonClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            // The main click action is now on the content, not the whole row
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Side: This part is clickable to show details
        Row(
            modifier = Modifier
                .weight(1f) // Takes up all available space, pushing the right side to the end
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = groupInfo.iconResId),
                contentDescription = "Group Icon",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 16.dp),
                tint = Color.Gray
            )
            Column {
                Text(
                    text = groupInfo.name,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                )
                Text(
                    text = groupInfo.courseCode,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )
            }
        }

        // Right Side: Member Count and Join Button are now in a Column
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = "${groupInfo.memberCount} members",
                style = TextStyle(fontSize = 14.sp, color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onButtonClick,
                enabled = !isJoined // The button is disabled if the user has already joined
            ) {
                // The text changes based on whether the user has joined
                Text(if (isJoined) "Joined" else "Join")
            }
        }
    }
}