package com.noisevisionproductions.playmeet.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.chat.ChatMessageModel;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for chat messages that uses FirebaseRecyclerAdapter.
 */
public class ChatMessageAdapter extends FirebaseRecyclerAdapter<ChatMessageModel, ChatMessageAdapter.ChatViewHolder> {
    private final Context context;
    private String currentUserId;
    private FirebaseHelper firebaseHelper;
    private static final int SENT_MESSAGE_TYPE = 1;
    private static final int RECEIVED_MESSAGE_TYPE = 2;

    public ChatMessageAdapter(@NonNull FirebaseRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
        setCurrentUserId();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageModel chatMessageModel = getItem(position);
        if (chatMessageModel.getUserId().equals(currentUserId)) {
            return SENT_MESSAGE_TYPE;
        } else {
            return RECEIVED_MESSAGE_TYPE;
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatMessageAdapter.ChatViewHolder holder, int position, @NonNull ChatMessageModel model) {
        setMessagesLookBasedOnLoggedUser(holder, model);
        bind(model, holder);
    }

    /**
     * Based on information if the message is sent or received by the current user,
     * I'm loading the right layout for the context.
     */
    @NonNull
    @Override
    public ChatMessageAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENT_MESSAGE_TYPE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent_design, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_received_design, parent, false);
        }
        return new ChatViewHolder(view);
    }

    /**
     * Setting up information which are shown on every chat message, like nickname, message or hour.
     */
    public void bind(@NonNull ChatMessageModel chatMessageModel, @NonNull ChatViewHolder holder) {
        DatabaseReference userReference = FirebaseDatabase
                .getInstance()
                .getReference("UserModel")
                .child(chatMessageModel.getUserId());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * If the user from message doesn't exists in DB (he could delete his account before),
             * then I'm changing the nickname for "Profile deleted".
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.usernameTextView.setText(chatMessageModel.getNickname());
                } else {
                    holder.usernameTextView.setText(context.getString(R.string.noNickname));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Realtime Database error", "Error while checking user existence " + error.getMessage());
            }
        });
        holder.messageTextView.setText(chatMessageModel.getMessage());
        holder.timestampTextView.setText(chatMessageModel.formatDate());
    }


    private void setCurrentUserId() {
        firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            this.currentUserId = firebaseHelper.getCurrentUser().getUid();
        }
    }

    /**
     * Loading users avatars from DB.
     */
    private void setMessagesLookBasedOnLoggedUser(@NonNull ChatViewHolder holder, @NonNull ChatMessageModel chatMessageModel) {
        if (chatMessageModel.getUserId().equals(currentUserId)) {
            firebaseHelper.getUserAvatar(context, currentUserId, holder.userAvatar);
        } else {
            firebaseHelper.getUserAvatar(context, chatMessageModel.getUserId(), holder.userAvatar);
        }
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView usernameTextView, messageTextView, timestampTextView;
        private final CircleImageView userAvatar;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            userAvatar = itemView.findViewById(R.id.userAvatar);

        }
    }
}