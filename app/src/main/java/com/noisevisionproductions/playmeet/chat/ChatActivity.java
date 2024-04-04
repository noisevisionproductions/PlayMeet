package com.noisevisionproductions.playmeet.chat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.adapters.ChatMessageAdapter;
import com.noisevisionproductions.playmeet.notifications.NotificationHelper;

/**
 * Class that is responsible for displaying chat messages in the given chat rooms.
 */
public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference messagesReference;
    private AppCompatEditText messageInputFromUser;
    private AppCompatImageButton sendMessageButton;
    private ProgressBar loadMorePostsIndicator;
    private ChatMessageAdapter chatMessageAdapter;
    @Nullable
    private String currentRoomId;
    @Nullable
    private FirebaseUser currentUser;
    private boolean messageSent = false;

    /**
     * Loading layout and its functionality.
     * Making sure that on the layout click, it hides the keyboard
     * and cancel the input focus.
     * Also, after that, making sure that it hides the keyboard after sending the message.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_and_history);

        setLayout();
        setRecyclerView();
        sendMessage();

        View layoutMain = findViewById(R.id.layoutMain);
        hideKeyboardOnLayoutClick(layoutMain);
        hideKeyboardAfterSendingMsg();
    }

    /**
     * Defining objects from chat layout.
     */
    private void setLayout() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentRoomId = getIntent().getStringExtra("roomId");
        recyclerView = findViewById(R.id.recycler_view_chat);
        loadMorePostsIndicator = findViewById(R.id.loadMorePostsIndicator);
        loadMorePostsIndicator.setVisibility(View.VISIBLE);
        messageInputFromUser = findViewById(R.id.messageInputFromUser);
        messageInputFromUser = findViewById(R.id.messageInputFromUser);
        sendMessageButton = findViewById(R.id.sendMessageButton);
    }

    /**
     * Setting up RecyclerView by retrieving messages from DB, by querying based on given chat room ID.
     * Creating query with ordering by message timestamp to ensure they are displayed in chronological order.
     */
    private void setRecyclerView() {
        if (currentRoomId != null) {
            messagesReference = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("ChatRooms")
                    .child(currentRoomId)
                    .child("messages");

            Query query = messagesReference.orderByChild("timestamp");

            FirebaseRecyclerOptions<ChatMessageModel> options = new FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                    .setQuery(query, ChatMessageModel.class)
                    .build();

            chatMessageAdapter = new ChatMessageAdapter(options, getApplicationContext());

            recyclerView.setAdapter(chatMessageAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

            /*
             *Due to nature how scrollToPosition works, I'm adding slight delay into automatic scroll to the bottom of the screen.
             * */
            recyclerView.postDelayed(() -> {
                recyclerView.scrollToPosition(chatMessageAdapter.getItemCount() - 1);
                loadMorePostsIndicator.setVisibility(View.GONE);
            }, 100);

            scrollToBottomOnMessageSent();
        }
    }

    /**
     * On send message button click, after making sure that given text is not empty, the message ID is assigned to the
     * sent message. When the message is sent, the notification for user which received the message, is shown.
     */
    private void sendMessage() {
        sendMessageButton.setOnClickListener(v -> {
            if (messageInputFromUser.getText() != null) {
                String messageText = messageInputFromUser.getText().toString();
                if (!messageText.isEmpty()) {
                    String messageId = messagesReference.push().getKey();
                    if (currentUser != null && messageId != null) {
                        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                        notificationHelper.getNotificationInfoForChatMessage(currentRoomId, messageText, currentUser.getUid(), currentUser.getDisplayName());

                        ChatMessageModel newMessage = new ChatMessageModel(messageId, currentUser.getUid(), currentUser.getDisplayName(), messageText, System.currentTimeMillis());
                        messagesReference.child(messageId).setValue(newMessage);
                        messageSent = true;
                        messageInputFromUser.setText("");
                        hideKeyboardAfterSendingMsg();
                    }
                }
            }
        });
    }

    /**
     * Triggering scrollToBottom method only when a new object (message) is added, so recycler view will always stay on the bottom.
     */
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

    /**
     * Scrolling RecyclerView to the last element on the list, by getting count of the item on the list.
     * If the count is more than 0, then smooth scrolling is triggered.
     */
    private void scrollToBottom() {
        if (recyclerView.getAdapter() != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            if (itemCount > 0) {
                recyclerView.smoothScrollToPosition(itemCount - 1);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        messageSent = false;
        chatMessageAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatMessageAdapter != null) {
            chatMessageAdapter.stopListening();
        }
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


    public void hideKeyboardOnLayoutClick(View view) {
        // Ustawienie dotknięcia dla widoku niemającego pola tekstowego
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hideKeyboardAfterSendingMsg();
                    v.performClick();  // Wywołanie performClick po zwolnieniu
                }
                return false;
            });
        }

        // Jeśli widok jest kontenerem, iterujemy przez jego dzieci i wykonujemy tę samą operację
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboardOnLayoutClick(innerView);
            }
        }
    }

    private void hideKeyboardAfterSendingMsg() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocusedView = getCurrentFocus();
        if (currentFocusedView != null) {
            currentFocusedView.clearFocus();
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
