package com.noisevisionproductions.playmeet.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noisevisionproductions.playmeet.Chat.ChatMessageModel;
import com.noisevisionproductions.playmeet.R;

public class ChatMessageAdapter extends FirebaseRecyclerAdapter<ChatMessageModel, ChatMessageAdapter.ChatViewHolder> /*RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder>*/ {

    public ChatMessageAdapter(@NonNull FirebaseRecyclerOptions<ChatMessageModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatMessageAdapter.ChatViewHolder holder, int position, @NonNull ChatMessageModel model) {
        setMessagesLookBasedOnLoggedUser(holder, model);
        holder.bind(model);
    }

    @NonNull
    @Override
    public ChatMessageAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_in_chat_design, parent, false);
        return new ChatViewHolder(view);
    }

    public void setMessagesLookBasedOnLoggedUser(ChatViewHolder holder, ChatMessageModel chatMessageModel) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            if (chatMessageModel.getUserId().equals(currentUserId)) {
                // ustawienie wyglądu wiadomości zależnie od tego czy wysłana czy odebrana
                holder.layoutOfMessage.setBackgroundColor(Color.BLUE);
            } else {
                holder.layoutOfMessage.setBackgroundColor(Color.GRAY);
            }
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView usernameTextView, messageTextView, timestampTextView;
        private final LinearLayoutCompat layoutOfMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            layoutOfMessage = itemView.findViewById(R.id.layoutOfMessage);
        }

        public void bind(ChatMessageModel chatMessageModel) {
            // ustawienie informacji jakie pojawiają się przy wiadomości - nickname, wiadomość, godzina
            usernameTextView.setText(chatMessageModel.getNickname());
            messageTextView.setText(chatMessageModel.getMessage());
            timestampTextView.setText(chatMessageModel.formatDate());
        }
    }

}