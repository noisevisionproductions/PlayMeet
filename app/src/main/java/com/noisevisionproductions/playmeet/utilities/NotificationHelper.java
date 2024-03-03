package com.noisevisionproductions.playmeet.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.noisevisionproductions.playmeet.R;

public class NotificationHelper {
    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void sendChatMessageNotification(String message) {
        if (notificationManager.getNotificationChannel("chat_messages") == null) {
            CharSequence name = "Chat Messages";
            String description = "Notifications about new chat messages";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("chat_messages", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        createNotification(message);
    }

    private void createNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "chat_messages")
                .setSmallIcon(R.drawable.message_received_background)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int notificationId = (int) System.currentTimeMillis();

        notificationManager.notify(notificationId, builder.build());
    }
}
