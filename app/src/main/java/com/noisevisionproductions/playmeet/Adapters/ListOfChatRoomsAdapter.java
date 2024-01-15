package com.noisevisionproductions.playmeet.Adapters;

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
import com.noisevisionproductions.playmeet.R;


public class ListOfChatRoomsAdapter extends FirebaseRecyclerAdapter<ChatRoomModel, ListOfChatRoomsAdapter.ViewHolder> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ChatRoomModel chatRoomModel);
    }

    public ListOfChatRoomsAdapter(OnItemClickListener listener, @NonNull FirebaseRecyclerOptions<ChatRoomModel> options) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatRoomModel model) {
        holder.bind(model, listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_design, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView username;
        private final TextView lastMessage;


        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tv_username);
            lastMessage = itemView.findViewById(R.id.tv_last_message);
        }

        public void bind(final ChatRoomModel chat, final OnItemClickListener listener) {
            // ustawianie ostatniej wiadomości z czatu wraz z nickiem użytkownika, który stworzył czat
            ChatMessageModel getLastMessage = chat.getLastMessage();
            if (getLastMessage != null && getLastMessage.getMessage() != null) {
                lastMessage.setText(chat.getLastMessage().getMessage());
                username.setText(getLastMessage.getNickname());
            } else {
                lastMessage.setText(R.string.noPrivateMessagInfo);
            }
            itemView.setOnClickListener(v -> listener.onItemClick(chat));
        }
    }
}
