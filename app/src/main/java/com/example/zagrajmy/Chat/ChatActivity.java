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
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private String roomIdNew;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_chat_and_history);

        openChat();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void openChat() {

        realm = Realm.getDefaultInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        messageInputFromUser = findViewById(R.id.messageInputFromUser);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        //createChat();
        setRecyclerView();
        showMessages();
    }

    public void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_chat);
        chatMessageAdapter = new ChatMessageAdapter(messagesList);
        recyclerView.setAdapter(chatMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    public void sendMessageToTheUser() {
        sendMessageButton.setOnClickListener(v -> realm.executeTransactionAsync(realm -> {

        }));
    }


    public void createChat() {

        sendMessageButton.setOnClickListener(v -> realm.executeTransactionAsync(realm -> {
            String roomId = getIntent().getStringExtra("roomId");
            PostCreating postCreating = realm.where(PostCreating.class).findFirst();
            User userFromRealm = realm.where(User.class)
                    .equalTo("userId", user.getUid())
                    .equalTo("nickName", user.getDisplayName())
                    .findFirst();
            assert postCreating != null;

            PrivateChatModel privateChatModel = realm.where(PrivateChatModel.class)
                    .equalTo("roomId", roomId)
                    .findFirst();

            assert userFromRealm != null;
            PrivateChatModel privateChatModelUserWhoCreatedPost = realm.where(PrivateChatModel.class)
                    .equalTo("userIdThatCreatedPost", userFromRealm.getUserId())
                    .findFirst();

            if (privateChatModel == null) {
                privateChatModel = realm.createObject(PrivateChatModel.class, roomId);
                privateChatModel.setUserIdThatCreatedPost(postCreating.getUserId());
                privateChatModel.setUser2(userFromRealm.getUserId());
                privateChatModel.setNickNameOfUser2(userFromRealm.getNickName());

                PrivateChatModel previousChat = realm.where(PrivateChatModel.class)
                        .equalTo("userIdThatCreatedPost", postCreating.getUserId())
                        .equalTo("user2", userFromRealm.getUserId())
                        .findFirst();

                if (previousChat != null) {
                    privateChatModel.getMessages().addAll(previousChat.getMessages());
                }
                if (privateChatModelUserWhoCreatedPost == null) {
                    privateChatModelUserWhoCreatedPost = realm.createObject(PrivateChatModel.class, roomIdNew);
                    privateChatModelUserWhoCreatedPost.setUserIdThatCreatedPost(postCreating.getUserId());
                    privateChatModelUserWhoCreatedPost.setUser2(userFromRealm.getUserId());
                    privateChatModelUserWhoCreatedPost.setNickNameOfUser2(userFromRealm.getNickName());
                }
            }


            ChatMessageModel message = realm.createObject(ChatMessageModel.class);

            message.getUsers().add(userFromRealm);
            message.setMessage(String.valueOf(messageInputFromUser.getText()));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                message.setTimestamp(LocalDateTime.now());
            }

            PrivateChatModel privateChatModelUser2 = realm.where(PrivateChatModel.class)
                    .equalTo("user2", userFromRealm.getUserId())
                    .findFirst();
            assert privateChatModelUser2 != null;
            realm.insertOrUpdate(privateChatModelUser2);
            privateChatModelUser2.getMessages().add(message);
            privateChatModelUser2.setLastMessage(message);


            if (privateChatModelUserWhoCreatedPost != null) {
                realm.insertOrUpdate(privateChatModelUserWhoCreatedPost);
                privateChatModelUserWhoCreatedPost.getMessages().add(message);
                privateChatModelUserWhoCreatedPost.setLastMessage(message);

            }
            hideKeyboardAfterSendingMsg();
        }));
    }

    public void showMessages() {
        PostCreating postCreating = realm.where(PostCreating.class).findFirst();

        assert postCreating != null;
        RealmResults<PrivateChatModel> privateChatModelListOfMessages = realm.where(PrivateChatModel.class)
                .equalTo("userIdThatCreatedPost", postCreating.getUserId())
                .or()
                .equalTo("user2", user.getUid())
                .findAll();

        for (PrivateChatModel privateChatModel : privateChatModelListOfMessages) {
            RealmList<ChatMessageModel> chatMessages = privateChatModel.getMessages();
            messagesList.addAll(realm.copyFromRealm(chatMessages));
        }
        chatMessageAdapter.notifyDataSetChanged();

    }

    public void hideKeyboardAfterSendingMsg() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(messageInputFromUser.getWindowToken(), 0);
        messageInputFromUser.setText("");
    }
}
