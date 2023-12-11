package com.example.zagrajmy.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Chat.ChatMessageModel;
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.User;

import java.util.List;

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
        ChatMessageModel chatMessageModel = this.chatMessageModel.get(position);
        holder.bind(chatMessageModel);
    }


    @Override
    public int getItemCount() {
        return chatMessageModel.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView usernameTextView, messageTextView, timestampTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
        }

        public void bind(ChatMessageModel chatMessageModel) {
            User user = chatMessageModel.getUsers().get(0);
            usernameTextView.setText(user.getNickName());
            messageTextView.setText(chatMessageModel.getMessage());
            timestampTextView.setText(chatMessageModel.getTimestamp().toString());
        }
    }
}
