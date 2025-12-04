package com.example.demounicdagroups.Data.Remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.demounicdagroups.MainActivity
import com.example.demounicdagroups.R
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import kotlin.random.Random

const val TAG = "FCM"

class MessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage){
        super.onMessageReceived(message)

        //notification
        message.notification?.let {
            showNotification(it)
        }

        //check if message contains a data payload
        if(message.data.isNotEmpty()) {
            handleDataMessage()
        }

    }

    private fun handleDataMessage(){
        Log.d(TAG,"handleDataMessage")
    }

    fun showNotification(message: RemoteMessage.Notification){

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE or FLAG_UPDATE_CURRENT )

        val channelId = "Default"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.unicda_group)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelName ="Firebase Messaging"

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        manager.notify(Random.nextInt(), notificationBuilder)

    }

}
