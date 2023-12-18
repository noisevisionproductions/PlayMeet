package com.example.zagrajmy.Chat;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Adapters.ChatMessageAdapter;
import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ChatActivity extends AppCompatActivity {
    private final List<ChatMessageModel> messagesList = new ArrayList<>();
    private Realm realm;
    private FirebaseUser user;
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

        user = FirebaseAuth.getInstance().getCurrentUser();

        messageInputFromUser = findViewById(R.id.messageInputFromUser);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        //createChat();
        sendMessageToTheUser();
        setRecyclerView();
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
            User userFromRealm = realm.where(User.class)
                    .equalTo("userId", user.getUid())
                    .equalTo("nickName", user.getDisplayName())
                    .findFirst();

            String uuid = UUID.randomUUID().toString();

            ChatMessageModel chatMessageModel = realm.createObject(ChatMessageModel.class, uuid);

            chatMessageModel.getUsers().add(userFromRealm);
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

            recyclerView.post(() -> recyclerView.scrollToPosition(messagesList.size() - 1));
            hideKeyboardAfterSendingMsg();
        }));
    }


    public void showMessages() {
        PostCreating postCreating = realm.where(PostCreating.class).findFirst();

        assert postCreating != null;
        String userId = postCreating.getUserId();

        RealmResults<PrivateChatModel> privateChatModels = realm.where(PrivateChatModel.class).findAllAsync();
        for (PrivateChatModel privateChatModel : privateChatModels) {
            String user2Id = privateChatModel.getUser2();

            RealmResults<PrivateChatModel> privateChatModelListOfMessages = realm.where(PrivateChatModel.class)
                    .beginGroup()
                    .equalTo("userIdThatCreatedPost", userId)
                    .equalTo("roomId", currentRoomId)
                    .endGroup()
                    .or()
                    .beginGroup()
                    .equalTo("user2", user2Id)
                    .equalTo("roomId", currentRoomId)
                    .endGroup()
                    .findAll();

            for (PrivateChatModel privateChatModelMessage : privateChatModelListOfMessages) {
                RealmList<ChatMessageModel> chatMessages = privateChatModelMessage.getMessages();
                messagesList.addAll(realm.copyFromRealm(chatMessages));
            }
        }

        chatMessageAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messagesList.size() - 1);
    }


    public void hideKeyboardAfterSendingMsg() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(messageInputFromUser.getWindowToken(), 0);
        messageInputFromUser.setText("");
    }


}
