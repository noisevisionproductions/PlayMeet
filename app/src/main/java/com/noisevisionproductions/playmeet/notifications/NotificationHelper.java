package com.noisevisionproductions.playmeet.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.ActivityMainMenu;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.chat.ChatRoomModel;
import com.noisevisionproductions.playmeet.firebase.FirebaseUserRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnTokenFound;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationHelper {
    private final Context context;
    private final NotificationManager notificationManager;
    private final String GROUP_KEY_CHAT_MESSAGES = "com.example.myapp.CHAT_MESSAGES";

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    protected void sendChatMessageNotification(String message, String title) {
        if (notificationManager.getNotificationChannel("chat_messages") == null) {
            CharSequence name = "Chat Messages";
            String description = "Notifications about new chat messages";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("chat_messages", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        createMessageNotification(message, title);
    }

    protected void createMessageNotification(String message, String title) {
        int notificationId = (int) System.currentTimeMillis();

        Intent intent = new Intent(context, ActivityMainMenu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent markAsReadIntent = new Intent(context, NotificationActionReceiver.class);
        markAsReadIntent.putExtra("action", "mark_as_read");
        markAsReadIntent.putExtra("notification_id", notificationId);
        PendingIntent markAsReadPendingIntent = PendingIntent.getBroadcast(context, notificationId, markAsReadIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "chat_messages")
                .setSmallIcon(R.drawable.icon_p_for_notification)
                .setGroup(GROUP_KEY_CHAT_MESSAGES)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(0, "Przeczytane", markAsReadPendingIntent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
        handleMultipleMessages();
    }

    private void handleMultipleMessages() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "chat_messages")
                .setContentTitle("Nowe wiadomości")
                .setContentText("Masz nowe wiadomości")
                .setSmallIcon(R.drawable.icon_p_for_notification)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("Wiadomość 1")
                        .addLine("Wiadomość 2")
                        .addLine("Wiadomość 3")
                        .addLine("Wiadomość 4")
                        .setBigContentTitle("Nowe wiadomości")
                        .setSummaryText("Masz nowe wiadomości"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup(GROUP_KEY_CHAT_MESSAGES)
                .setGroupSummary(true);

        notificationManager.notify(0, builder.build());
    }

    public void getNotificationInfoForChatMessage(String roomId, String message, String currentUserId, String currentUserName) {
        DatabaseReference chatRoomReference = FirebaseDatabase.getInstance().getReference("ChatRooms").child(roomId);
        FirebaseUserRepository firebaseUserRepository = new FirebaseUserRepository();
        chatRoomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatRoomModel chatRoomModel = snapshot.getValue(ChatRoomModel.class);
                if (chatRoomModel != null) {
                    Map<String, Boolean> userIdsFromChatRoom = chatRoomModel.getParticipants();
                    userIdsFromChatRoom.remove(currentUserId);

                    for (String userId : userIdsFromChatRoom.keySet()) {
                        firebaseUserRepository.getUserToken(userId, new OnTokenFound() {
                            @Override
                            public void onTokenFound(String token) {
                                createNotificationInJsonFile(message, currentUserId, token, currentUserName);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("token not found error", "token not found error " + e.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.e("ChatRoom info for notification error", "ChatRoom info for notification error " + e.getMessage());
            }
        });
    }

    public void sendJoinedNotification(String userId) {
        FirebaseUserRepository firebaseUserRepository = new FirebaseUserRepository();
        firebaseUserRepository.getUserToken(userId, new OnTokenFound() {
            @Override
            public void onTokenFound(String token) {
                try {
                    JSONObject jsonObject = new JSONObject();

                    JSONObject dataObject = new JSONObject();
                    dataObject.put("message", "Wejdź do aplikacji, aby poznać więcej szczegółów.");
                    dataObject.put("title", "Ktoś dołączył do Twojej aktywności!");

                    jsonObject.put("data", dataObject);
                    jsonObject.put("to", token);

                    callApi(jsonObject);
                } catch (JSONException e) {
                    Log.e("Creating JsonFile error", "Creating JsonFile error " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Getting user token error", "Getting user token error " + e.getMessage());
            }
        });
    }

    private void createNotificationInJsonFile(String message, String currentUserId, String otherUserToken, String currentUserName) {
        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject dataObject = new JSONObject();
            dataObject.put("message", message);
            dataObject.put("userId", currentUserId);
            dataObject.put("title", "Wiadomość od: " + currentUserName);

            jsonObject.put("data", dataObject);
            jsonObject.put("to", otherUserToken);

            callApi(jsonObject);
        } catch (JSONException e) {
            Log.e("Creating JsonFile error", "Creating JsonFile error " + e.getMessage());
        }
    }

    private void callApi(JSONObject jsonObject) {
        MediaType json = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), json);
        String authorizationKey = getAuth();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", authorizationKey)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Call Api error", "Call Api error " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String responseString = response.body().string();
                        Log.d("API Response", "Response from server: " + responseString);
                    }
                } else {
                    Log.e("API Response", "Request failed: " + response.code());
                }
            }
        });
    }

    private String getAuth() {
        Properties properties = new Properties();
        String value = "";
        try {
            InputStream inputStream = context.getAssets().open("key.properties");
            properties.load(inputStream);
            value = properties.getProperty("firebaseAuth");
        } catch (IOException e) {
            Log.e("API Response", "Request failed: " + e.getMessage());
        }
        return value;
    }
}
