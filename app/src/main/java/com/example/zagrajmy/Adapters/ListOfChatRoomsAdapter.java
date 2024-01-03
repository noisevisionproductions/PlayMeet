package com.example.zagrajmy.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Chat.PrivateChatModel;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;

import java.util.List;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class ListOfChatRoomsAdapter extends RecyclerView.Adapter<ListOfChatRoomsAdapter.ViewHolder> {
    private final List<PrivateChatModel> chats;
    private final OnItemClickListener listener;
    private final Realm realm;

    public interface OnItemClickListener {
        void onItemClick(PrivateChatModel chat);
    }

    public ListOfChatRoomsAdapter(List<PrivateChatModel> chats, OnItemClickListener listener) {
        this.chats = chats;
        this.listener = listener;
        this.realm = Realm.getDefaultInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_for_chatroom_list, parent, false);
        return new ViewHolder(view, realm);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(chats.get(position), listener);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView lastMessage;
        Realm realm;
        User user;
        App realmApp;

        public ViewHolder(View itemView, Realm realm) {
            super(itemView);
            this.realm = realm;
            username = itemView.findViewById(R.id.tv_username);
            lastMessage = itemView.findViewById(R.id.tv_last_message);
            realmApp = RealmAppConfig.getApp();
            user = realmApp.currentUser();
        }

        public void bind(final PrivateChatModel chat, final OnItemClickListener listener) {
            username.setText(chat.getNickNameOfUser2());
            //lastMessage.setText(chat.getLastMessage().getMessage());

            if (chat.getLastMessage() != null) {
                lastMessage.setText(chat.getLastMessage().getMessage());
            } else {
                lastMessage.setText(R.string.noPrivateMessagInfo);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(chat));
        }

    }
}
