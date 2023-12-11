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
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ChatActivity extends AppCompatActivity {
    private final List<ChatMessageModel> messagesList = new ArrayList<>();
    private Realm realm;
    private FirebaseUser user;
    private AppCompatEditText messageInputFromUser;
    private AppCompatImageButton sendMessageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_chat_and_history);

        openChat();

    }

    public void openChat() {

        realm = Realm.getDefaultInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        messageInputFromUser = findViewById(R.id.messageInputFromUser);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        createChat();
        setRecyclerView();
        showMessages();
    }

    public void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_chat);
        ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(messagesList);
        recyclerView.setAdapter(chatMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        chatMessageAdapter.notifyDataSetChanged();
    }

    public void createChat() {

        sendMessageButton.setOnClickListener(v -> realm.executeTransactionAsync(realm -> {
            User userFromRealm = realm.where(User.class)
                    .equalTo("userId", user.getUid())
                    .equalTo("nickName", user.getDisplayName())
                    .findFirst();
            if (user != null) {
                RealmList<User> usersList = new RealmList<>();
                usersList.add(userFromRealm);
                ChatMessageModel message = realm.createObject(ChatMessageModel.class);

                message.setUsers(usersList);
                message.setMessage(String.valueOf(messageInputFromUser.getText()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    message.setTimestamp(LocalDateTime.now());
                }

            }

            messageInputFromUser.setText("");

            /* chowa klawiature po wyslaniu wiadomosci*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(messageInputFromUser.getWindowToken(), 0);


           /* ChatActivityModel chatActivityModel = realm.where(ChatActivityModel.class).equalTo("chatId", "0").findFirst();
            assert chatActivityModel != null;
            chatActivityModel.getMessages().add(message);*/
        }));
    }

    public void showMessages() {
        User userFromRealm = realm.where(User.class).equalTo("userId", user.getUid()).findFirst();

        assert userFromRealm != null;
        RealmResults<ChatMessageModel> chatMessages = realm.where(ChatMessageModel.class)
                .findAll();
        messagesList.addAll(realm.copyFromRealm(chatMessages));
        realm.close();
    }

}
