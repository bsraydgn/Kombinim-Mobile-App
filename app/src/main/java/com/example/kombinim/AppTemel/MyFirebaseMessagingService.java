package com.example.kombinim.AppTemel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        BaslaActivity.vb.getDbref("Kullanicilar")
                .child(BaslaActivity.vb.getUserID())
                .child("devicetoken")
                .setValue(s);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        sendNotification(Objects.requireNonNull(remoteMessage.getNotification()));
    }

    private void sendNotification(RemoteMessage.Notification message) {
        Intent intent = new Intent(this, BaslaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //noinspection deprecation
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setLights(getResources().getColor(R.color.sarı),1000,500)
                .setContentText(message.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000,1000})
                .setContentIntent(pendingIntent);
        if(message.getTitle()!=null){
            notificationBuilder.setContentTitle(message.getTitle());
        }else{
            notificationBuilder.setContentTitle(getResources().getString(R.string.app_name));
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
