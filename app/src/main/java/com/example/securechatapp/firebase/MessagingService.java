package com.example.securechatapp.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.securechatapp.R;
import com.example.securechatapp.activities.ChatActivity;
import com.example.securechatapp.utilities.Global;
import com.example.securechatapp.utilities.UserList;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    public static final String CHANNEL_ID = "Notification";
    public static final String CHANNEL_NAME = "Notification";
    public static final String CHANNEL_DESC = "This is a notification";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);


    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        UserList userList = new UserList();
        Global.init();
        userList.setUid(remoteMessage.getData().get("userId"));
        userList.setName(remoteMessage.getData().get("userName"));
        userList.setFcmToken(remoteMessage.getData().get("fcmToken"));

        int notificationId = new Random().nextInt();
        String channel = "chat_message";
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("user", userList);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel);
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        builder.setContentTitle(userList.getName());
        builder.setContentText(Global.decrypt(remoteMessage.getData().get("message")));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(Global.decrypt(remoteMessage.getData().get("message"))));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel2 = new NotificationChannel(channel,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel2);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());

    }
}
