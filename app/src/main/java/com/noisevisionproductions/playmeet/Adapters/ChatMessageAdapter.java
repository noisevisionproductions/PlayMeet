package com.noisevisionproductions.playmeet.Adapters;

import android.content.Context;
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
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessageAdapter extends FirebaseRecyclerAdapter<ChatMessageModel, ChatMessageAdapter.ChatViewHolder> {
    private final Context context;

    public ChatMessageAdapter(@NonNull FirebaseRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatMessageAdapter.ChatViewHolder holder, int position, @NonNull ChatMessageModel model) {
        setMessagesLookBasedOnLoggedUser(holder, model);
        bind(model, holder);
    }

    @NonNull
    @Override
    public ChatMessageAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_in_chat_design, parent, false);
        return new ChatViewHolder(view);
    }

    public void setMessagesLookBasedOnLoggedUser(ChatViewHolder holder, ChatMessageModel chatMessageModel) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        String currentUserId = firebaseHelper.getCurrentUser().getUid();
        if (chatMessageModel.getUserId().equals(currentUserId)) {
            firebaseHelper.getUserAvatar(context, currentUserId, holder.userAvatar);
            // ustawienie wyglądu wiadomości zależnie od tego czy wysłana czy odebrana
            //holder.layoutOfMessage.setBackgroundColor(Color.BLUE);
        } else {
            firebaseHelper.getUserAvatar(context, chatMessageModel.getUserId(), holder.userAvatar);
            holder.layoutOfMessage.setBackgroundColor(Color.GRAY);
        }
    }

    public void bind(ChatMessageModel chatMessageModel, ChatViewHolder holder) {
        // ustawienie informacji jakie pojawiają się przy wiadomości - nickname, wiadomość, godzina
        holder.usernameTextView.setText(chatMessageModel.getNickname());
        holder.messageTextView.setText(chatMessageModel.getMessage());
        holder.timestampTextView.setText(chatMessageModel.formatDate());
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView usernameTextView, messageTextView, timestampTextView;
        private final LinearLayoutCompat layoutOfMessage;
        private final CircleImageView userAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            layoutOfMessage = itemView.findViewById(R.id.layoutOfMessage);
            userAvatar = itemView.findViewById(R.id.userAvatar);
        }
    }
}