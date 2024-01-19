package com.noisevisionproductions.playmeet.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
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
    private String currentRoomId, userIdThatCreatedPost;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_and_history);

        String roomId = getIntent().getStringExtra("roomId");
        setCurrentRoomId(roomId);

        setupFirebase();
        setRecyclerView();
        openChat();

        handleBackPressed();
    }

    private void setupFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && currentRoomId != null) {
            String currentUserId = currentUser.getUid();
            chatRoomReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUserId).child("ChatRooms").child(currentRoomId);
            messagesReference = chatRoomReference.child("ChatMessages");

            // pobieram ID użytkownika, który stworzył post
            chatRoomReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        ChatRoomModel chatRoomModel = snapshot.getValue(ChatRoomModel.class);
                        if (chatRoomModel != null) {
                            userIdThatCreatedPost = chatRoomModel.getUserIdThatCreatedPost();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_chat);
        // zaraz po otwarciu czatu, przenosi użytkownika na sam dół ekranu, gdzie są ostatnio wysłane wiadomości
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                scrollToBottom();
                recyclerView.removeOnLayoutChangeListener(this);
            }
        });
        chatMessageAdapter = new ChatMessageAdapter(new FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(messagesReference, ChatMessageModel.class)
                .build(), getApplicationContext());
        recyclerView.setAdapter(chatMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void scrollToBottom() {
        if (recyclerView.getAdapter() != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            if (itemCount > 0) {
                recyclerView.smoothScrollToPosition(itemCount - 1);
            }
        }
    }

    private void setCurrentRoomId(String roomId) {
        this.currentRoomId = roomId;
    }

    private void openChat() {
        messageInputFromUser = findViewById(R.id.messageInputFromUser);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        sendMessageToTheUser();
    }

    private void sendMessageToTheUser() {
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
                                    if (senderNickname != null) {
                                        if (!TextUtils.isEmpty(messageText)) {
                                            long timestamp = System.currentTimeMillis();
                                            ChatMessageModel chatMessageModel = new ChatMessageModel(uniqueMessageId, senderId, senderNickname, messageText, timestamp);

                                            messagesReference.push().setValue(chatMessageModel);
                                            chatRoomReference.child("lastMessage").setValue(chatMessageModel);

                                            FirebaseDatabase.getInstance().getReference().child("UserModel")
                                                    .child(userIdThatCreatedPost).child("ChatRooms").child(currentRoomId).child("ChatMessages")
                                                    .push().setValue(chatMessageModel);
                                            FirebaseDatabase.getInstance().getReference().child("UserModel")
                                                    .child(userIdThatCreatedPost).child("ChatRooms").child(currentRoomId).child("lastMessage")
                                                    .setValue(chatMessageModel);
                                            scrollToBottom();
                                            hideKeyboardAfterSendingMsg();
                                        }
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
    public void onDestroy() {
        super.onDestroy();
        if (chatMessageAdapter != null) {
            chatMessageAdapter.stopListening();
        }
    }

    private void hideKeyboardAfterSendingMsg() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(messageInputFromUser.getWindowToken(), 0);
        messageInputFromUser.setText("");
    }

    private void handleBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), MainMenuPosts.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
