package com.noisevisionproductions.playmeet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.noisevisionproductions.playmeet.Chat.ChatMessageModel;
import com.noisevisionproductions.playmeet.Chat.ChatRoomModel;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ListOfChatRoomsAdapter extends FirebaseRecyclerAdapter<ChatRoomModel, ListOfChatRoomsAdapter.ViewHolder> {

    private final OnItemClickListener listener;
    private final Context context;

    public interface OnItemClickListener {
        void onItemClick(ChatRoomModel chatRoomModel);
    }

    public ListOfChatRoomsAdapter(OnItemClickListener listener, @NonNull FirebaseRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatRoomModel model) {
        //  holder.lastMessage.setText((CharSequence) model.getLastMessage());
        bind(holder, model, listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_design, parent, false);
        return new ViewHolder(view);
    }

    public void bind(ViewHolder holder, final ChatRoomModel chat, final OnItemClickListener listener) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        String currentUserId = firebaseHelper.getCurrentUser().getUid();
        // ustawianie ostatniej wiadomości z czatu wraz z nickiem użytkownika, który stworzył czat
        ChatMessageModel getLastMessage = chat.getLastMessage();
        String otherUserId;
        if (chat.getUser2() != null && chat.getUser2().equals(currentUserId)) {
            otherUserId = chat.getUserIdThatCreatedPost();
        } else {
            otherUserId = chat.getUser2();
        }
        firebaseHelper.getUserAvatar(context, otherUserId, holder.userAvatar);
        holder.lastMessage.setText(chat.getLastMessage().getMessage());
        holder.username.setText(chat.getLastMessage().getNickname());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(chat));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView username;
        private final TextView lastMessage;
        private final CircleImageView userAvatar;


        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tv_username);
            lastMessage = itemView.findViewById(R.id.tv_last_message);
            userAvatar = itemView.findViewById(R.id.userAvatar);
        }
    }
}
