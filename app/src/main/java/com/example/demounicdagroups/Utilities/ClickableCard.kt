package com.example.demounicdagroups.Utilities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.demounicdagroups.ui.theme.Blue1

@Composable
fun ColumnReuse(modifier: Modifier = Modifier,
                groupInfo: GroupInfo,
                onClick: () -> Unit,){
    var showDialog = rememberSaveable() { }
    Column(
        modifier = Modifier
            .width(350.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = Blue1)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = groupInfo.imageResId),
            contentDescription = groupInfo.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
