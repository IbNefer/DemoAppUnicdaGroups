package com.example.demounicdagroups.features.notification

import android.Manifest
import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog(modifier: Modifier = Modifier) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        val notificationPermissionState = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        )


        if (!notificationPermissionState.status.isGranted) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Enable Notifications") },
                text = {

                    val textToShow = if (notificationPermissionState.status.shouldShowRationale) {
                        "We need notifications to send you important updates about your study groups. Please allow access."
                    } else {
                        "Please enable notifications to stay updated with your groups."
                    }
                    Text(textToShow)
                },
                confirmButton = {
                    Button(onClick = {
                        notificationPermissionState.launchPermissionRequest()
                    }) {
                        Text("Allow")
                    }
                },
                modifier = modifier
            )
        }
    }
}