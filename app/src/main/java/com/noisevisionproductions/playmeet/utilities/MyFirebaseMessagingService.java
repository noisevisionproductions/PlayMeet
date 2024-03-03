package com.noisevisionproductions.playmeet.utilities;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private NotificationHelper notificationHelper;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        notificationHelper = new NotificationHelper(this);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();
            if (message != null) {
                notificationHelper.sendChatMessageNotification(message);
            }
        }
    }
}
