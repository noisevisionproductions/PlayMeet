package com.example.zagrajmy.Chat;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.zagrajmy.Design.SidePanelBaseActivity;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

import io.realm.Realm;

public class ChatActivity extends SidePanelBaseActivity {

    private AppCompatEditText messageInputFromUser;
    private AppCompatImageButton sendMessageButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_and_history);

        messageInputFromUser = findViewById(R.id.messageInputFromUser);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        createChat();

        setupDrawerLayout();
        setupNavigationView();
    }

    public void createChat() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sendMessageButton.setOnClickListener(v -> Realm.getDefaultInstance().executeTransactionAsync(realm -> {
            ChatMessageModel message = realm.createObject(ChatMessageModel.class);
            assert user != null;
            message.setUserId(user.getUid());
            message.setMessage(String.valueOf(messageInputFromUser.getText()));
            message.setTimestamp(new Date());

            ChatModel chatModel = realm.where(ChatModel.class).equalTo("chatId", 0).findFirst();
            assert chatModel != null;
            chatModel.getMessages().add(message);
        }));
    }

}
