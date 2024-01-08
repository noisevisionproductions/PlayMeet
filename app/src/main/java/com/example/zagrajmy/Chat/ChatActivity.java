package com.example.zagrajmy.Chat;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Adapters.ChatMessageAdapter;
import com.example.zagrajmy.DataManagement.ChatMessageDiffUtilCallback;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.UserManagement.UserModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;

public class ChatActivity extends AppCompatActivity {
    private final List<ChatMessageModel> messagesList = new ArrayList<>();
    private Realm realm;
    private io.realm.mongodb.User user;
    private AppCompatEditText messageInputFromUser;
    private AppCompatImageButton sendMessageButton;
    private ChatMessageAdapter chatMessageAdapter;
    private RecyclerView recyclerView;
    private String currentRoomId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        String roomId = getIntent().getStringExtra("roomId");
        setCurrentRoomId(roomId);

        setContentView(R.layout.activity_chat_and_history);

        openChat();
    }

    public void setCurrentRoomId(String roomId) {
        this.currentRoomId = roomId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void openChat() {
        App realmApp = RealmAppConfig.getApp();
        user = realmApp.currentUser();

        messageInputFromUser = findViewById(R.id.messageInputFromUser);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        setRecyclerView();
        sendMessageToTheUser();
        showMessages();
    }

    public void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_chat);
        chatMessageAdapter = new ChatMessageAdapter(messagesList);
        recyclerView.setAdapter(chatMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    public void sendMessageToTheUser() {
        sendMessageButton.setOnClickListener(v -> realm.executeTransactionAsync(realm -> {
            UserModel userModel = realm.where(UserModel.class)
                    .equalTo("userId", user.getId())
                    .findFirst();

            if (userModel != null) {
                UserModel userModelFromRealm = realm.where(UserModel.class)
                        .equalTo("userId", user.getId())
                        .equalTo("nickName", userModel.getNickName())
                        .findFirst();

                String uuid = UUID.randomUUID().toString();

                ChatMessageModel chatMessageModel = realm.createObject(ChatMessageModel.class, uuid);

                chatMessageModel.getUsers().add(userModelFromRealm);
                chatMessageModel.setMessage(String.valueOf(messageInputFromUser.getText()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    chatMessageModel.setTimestamp(LocalDateTime.now());
                }

                PrivateChatModel privateChatModel = realm.where(PrivateChatModel.class)
                        .equalTo("roomId", currentRoomId)
                        .findFirst();
                if (privateChatModel != null) {
                    privateChatModel.getMessages().add(chatMessageModel);
                    realm.insertOrUpdate(chatMessageModel);
                }

                if (!messagesList.isEmpty()) {
                    recyclerView.post(() -> recyclerView.smoothScrollToPosition(messagesList.size() - 1));
                }
                hideKeyboardAfterSendingMsg();
            }
        }, this::showMessages));
    }

    public void showMessages() {
        // pobieranie wszystkich postów
        RealmResults<PostCreating> allPosts = realm.where(PostCreating.class).findAll();

        if (!allPosts.isEmpty()) {
            List<String> userIds = new ArrayList<>();

            // przeszukiwanie wszystkich postow i dodawanie Id uzytkowników do listy
            for (PostCreating postCreating : allPosts) {
                userIds.add(postCreating.getUserId());
            }

            // pobieranie wszystkich wiadomości
            RealmResults<PrivateChatModel> allMessages = realm.where(PrivateChatModel.class).findAll();
            List<ChatMessageModel> newMessagesList = new ArrayList<>();

            // filtrowanie pobranych wiadomości
            for (PrivateChatModel privateChatModel : allMessages) {
                // sprawdzenie, czy userId jest powiązane z danym chatRoomem
                if ((userIds.contains(privateChatModel.getUserIdThatCreatedPost()) || userIds.contains(privateChatModel.getUser2()))
                        && privateChatModel.getRoomId().equals(currentRoomId)) {
                    RealmList<ChatMessageModel> chatMessages = privateChatModel.getMessages();
                    newMessagesList.addAll(realm.copyFromRealm(chatMessages));
                }
            }

            updateMessagesUtilDiff(newMessagesList);
            if (!messagesList.isEmpty()) {
                recyclerView.post(() -> recyclerView.smoothScrollToPosition(messagesList.size() - 1));
            }
        }
    }

    public void updateMessagesUtilDiff(List<ChatMessageModel> newMessagesList) {
        ChatMessageDiffUtilCallback diffUtilCallback = new ChatMessageDiffUtilCallback(messagesList, newMessagesList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        messagesList.clear();
        messagesList.addAll(newMessagesList);
        diffResult.dispatchUpdatesTo(chatMessageAdapter);
        if (!messagesList.isEmpty()) {
            recyclerView.post(() -> recyclerView.smoothScrollToPosition(messagesList.size() - 1));
        }
    }

    public void hideKeyboardAfterSendingMsg() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(messageInputFromUser.getWindowToken(), 0);
        messageInputFromUser.setText("");
    }
}
