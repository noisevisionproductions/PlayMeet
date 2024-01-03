package com.example.zagrajmy.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Chat.ChatMessageModel;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.UserManagement.UserModel;

import java.util.List;
import java.util.Objects;

import io.realm.mongodb.App;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder> {
    private final List<ChatMessageModel> chatMessageModel;

    public ChatMessageAdapter(List<ChatMessageModel> chatMessageAdapter) {
        this.chatMessageModel = chatMessageAdapter;
    }

    @NonNull
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_in_chat_design, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        setMessagesLookBasedOnLoggedUser(holder, position);
        holder.itemView.setAlpha(0.0f);
        holder.itemView.animate().alpha(1.0f).setDuration(200).start();
        ChatMessageModel chatMessageModel = this.chatMessageModel.get(position);
        holder.bind(chatMessageModel);
    }

    @Override
    public int getItemCount() {
        return chatMessageModel.size();
    }

    public void setMessagesLookBasedOnLoggedUser(ChatViewHolder holder, int position) {
        ChatMessageModel chatMessageModel = this.chatMessageModel.get(position);

        App realmApp = RealmAppConfig.getApp();
        io.realm.mongodb.User user = realmApp.currentUser();

        if (user != null && Objects.requireNonNull(chatMessageModel.getUsers().get(0)).getUserId().equals(user.getId())) {
            holder.layoutOfMessage.setBackgroundColor(Color.BLUE);
        } else {
            holder.layoutOfMessage.setBackgroundColor(Color.GRAY);
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView usernameTextView, messageTextView, timestampTextView;
        LinearLayoutCompat layoutOfMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            layoutOfMessage = itemView.findViewById(R.id.layoutOfMessage);
        }

        public void bind(ChatMessageModel chatMessageModel) {
            UserModel userModel = chatMessageModel.getUsers().get(0);
            assert userModel != null;
            usernameTextView.setText(userModel.getNickName());
            messageTextView.setText(chatMessageModel.getMessage());
            timestampTextView.setText(chatMessageModel.getTimestamp());
        }
    }
}
