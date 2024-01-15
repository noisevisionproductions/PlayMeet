package com.noisevisionproductions.playmeet.Chat;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.Adapters.ChatMessageAdapter;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;

import java.util.Objects;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference messagesReference;
    private DatabaseReference chatRoomReference;
    private AppCompatEditText messageInputFromUser;
    private AppCompatImageButton sendMessageButton;
    private ChatMessageAdapter chatMessageAdapter;
    private String currentRoomId;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_and_history);

        String roomId = getIntent().getStringExtra("roomId");
        setCurrentRoomId(roomId);

        setupFirebase();
    }

    private void setupFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && currentRoomId != null) {
            messagesReference = FirebaseDatabase.getInstance().getReference("ChatMessages").child(currentRoomId);
            chatRoomReference = FirebaseDatabase.getInstance().getReference("ChatRooms").child(currentRoomId);

            if (messagesReference != null) {
                setRecyclerView();
                openChat();
            }
        }
    }

    public void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_chat);
        chatMessageAdapter = new ChatMessageAdapter(new FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(messagesReference, ChatMessageModel.class)
                .build());
        recyclerView.setAdapter(chatMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void setCurrentRoomId(String roomId) {
        this.currentRoomId = roomId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void openChat() {
        messageInputFromUser = findViewById(R.id.messageInputFromUser);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        sendMessageToTheUser();
    }

    public void sendMessageToTheUser() {
        sendMessageButton.setOnClickListener(v -> {
            String uniqueMessageId = UUID.randomUUID().toString();
            String messageText = Objects.requireNonNull(messageInputFromUser.getText()).toString();
            String senderId = currentUser.getUid();

            FirebaseDatabase.getInstance().getReference("UserModel").child(senderId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                UserModel senderUser = snapshot.getValue(UserModel.class);
                                if (senderUser != null) {
                                    String senderNickname = senderUser.getNickname();
                                    if (senderNickname != null && !TextUtils.isEmpty(messageText)) {
                                        long timestamp = System.currentTimeMillis();
                                        ChatMessageModel chatMessageModel = new ChatMessageModel(uniqueMessageId, senderId, senderNickname, messageText, timestamp);

                                        DatabaseReference newMessageReference = messagesReference.push();
                                        newMessageReference.setValue(chatMessageModel);

                                        chatRoomReference.child("lastMessage").setValue(chatMessageModel);

                                        hideKeyboardAfterSendingMsg();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Nie ustawiono nicku!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        chatMessageAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatMessageAdapter.stopListening();
    }

    public void hideKeyboardAfterSendingMsg() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(messageInputFromUser.getWindowToken(), 0);
        messageInputFromUser.setText("");
    }
}
