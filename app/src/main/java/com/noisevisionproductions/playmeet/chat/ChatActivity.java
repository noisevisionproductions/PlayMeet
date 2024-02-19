package com.noisevisionproductions.playmeet.chat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.adapters.ChatMessageAdapter;
import com.noisevisionproductions.playmeet.R;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference messagesReference;
    private AppCompatEditText messageInputFromUser;
    private AppCompatImageButton sendMessageButton;
    private ProgressBar loadMorePostsIndicator;
    private ChatMessageAdapter chatMessageAdapter;
    @Nullable
    private String currentRoomId;
    @Nullable
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private boolean messageSent = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_and_history);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        currentRoomId = getIntent().getStringExtra("roomId");
        setRecyclerView();
        openChat();

        sendMessage();
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_chat);
        loadMorePostsIndicator = findViewById(R.id.loadMorePostsIndicator);
        loadMorePostsIndicator.setVisibility(View.VISIBLE);

        if (currentRoomId != null) {
            messagesReference = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(currentRoomId).child("messages");

            Query query = messagesReference.orderByChild("timestamp");

            FirebaseRecyclerOptions<ChatMessageModel> options = new FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                    .setQuery(query, ChatMessageModel.class)
                    .build();

            chatMessageAdapter = new ChatMessageAdapter(options, getApplicationContext());

            recyclerView.setAdapter(chatMessageAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

            // z powodu natury jak działa scrollToPosition, dodaję lekkie opóźnienie automatycznego scrollu na sam dół listy
            recyclerView.postDelayed(() -> {
                recyclerView.scrollToPosition(chatMessageAdapter.getItemCount() - 1);
                loadMorePostsIndicator.setVisibility(View.GONE);
            }, 100);

            scrollToBottomOnMessageSent();
        }
    }

    private void sendMessage() {
        sendMessageButton.setOnClickListener(v -> {
            if (messageInputFromUser.getText() != null) {
                String messageText = messageInputFromUser.getText().toString();
                if (!messageText.isEmpty()) {
                    String messageId = messagesReference.push().getKey();
                    if (currentUser != null) {
                        ChatMessageModel newMessage = new ChatMessageModel(messageId, currentUser.getUid(), currentUser.getDisplayName(), messageText, System.currentTimeMillis());

                        if (messageId != null) {
                            messagesReference.child(messageId).setValue(newMessage);
                            messageSent = true;
                        }
                        messageInputFromUser.setText("");
                        hideKeyboardAfterSendingMsg();
                    }
                }
            }
        });
    }

    private void scrollToBottom() {
        if (recyclerView.getAdapter() != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            if (itemCount > 0) {
                recyclerView.smoothScrollToPosition(itemCount - 1);
            }
        }
    }

    private void scrollToBottomOnMessageSent() {
        messagesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                scrollToBottom();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase RealmTime Database error", "Scrolling user messages to the bottom " + error.getMessage());
            }
        });
    }

    private void openChat() {
        messageInputFromUser = findViewById(R.id.messageInputFromUser);
        sendMessageButton = findViewById(R.id.sendMessageButton);
    }

    @Override
    public void onStart() {
        super.onStart();
        messageSent = false;
        chatMessageAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!messageSent) {
            messagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        if (currentRoomId != null) {
                            FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(currentRoomId).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase Save Error", "Checking if chat room has messages " + error.getMessage());
                }
            });
        }
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
}
