package com.noisevisionproductions.playmeet.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

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

        if (notificationHelper == null) {
            notificationHelper = new NotificationHelper(this);
        }

        if (!remoteMessage.getData().isEmpty()) {
            // Dane można przetworzyć nawet gdy aplikacja jest w tle
            Map<String, String> data = remoteMessage.getData();
            String message = data.get("message");
            String title = data.get("title");
            if (message != null) {
                notificationHelper.sendChatMessageNotification(message, title);
            }
        }
    }
}
