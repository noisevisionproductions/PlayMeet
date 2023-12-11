package com.example.zagrajmy.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Chat.PrivateChatModel;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.realm.Realm;

public class ListOfChatRoomsAdapter extends RecyclerView.Adapter<ListOfChatRoomsAdapter.ViewHolder> {

    private final List<PrivateChatModel> chats;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PrivateChatModel chat);
    }

    public ListOfChatRoomsAdapter(List<PrivateChatModel> chats, OnItemClickListener listener) {
        this.chats = chats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_for_chatroom_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(chats.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView lastMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tv_username);
            lastMessage = itemView.findViewById(R.id.tv_last_message);
        }

        public void bind(final PrivateChatModel chat, final OnItemClickListener listener) {
            Realm realm = Realm.getDefaultInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            username.setText(chat.getUserIdThatCreatedPost());
            lastMessage.setText(chat.getLastMessage().getMessage());

            itemView.setOnClickListener(v -> listener.onItemClick(chat));
        }
    }
}
