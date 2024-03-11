package com.noisevisionproductions.playmeet.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.chat.ChatMessageModel;
import com.noisevisionproductions.playmeet.chat.ChatRoomModel;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;

import java.util.Map;

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
        getChatRoomData(holder, model);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_design, parent, false);
        return new ViewHolder(view);
    }

    private void getChatRoomData(@NonNull ViewHolder holder, @NonNull final ChatRoomModel chat) {
        setLoading(true, holder);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {

            String currentUserId = firebaseHelper.getCurrentUser().getUid();

            // pobieram mapę uczestników czatu
            Map<String, Boolean> participants = chat.getParticipants();

            // ustawianie ostatniej wiadomości z czatu wraz z nickiem użytkownika, który stworzył czat
            String otherUserId = findOtherUserId(participants, currentUserId);

            holder.itemView.setOnClickListener(v -> listener.onItemClick(chat));
            if (otherUserId != null) {
                //    holder.timestampTextView.setText(chat.formatDate());
                firebaseHelper.getUserAvatar(context, otherUserId, holder.userAvatar);
                firebaseHelper.getUserNickName(otherUserId, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String nickname = snapshot.getValue(String.class);
                            if (nickname != null) {
                                setLoading(false, holder);
                                holder.username.setText(nickname);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase RealmTime Database error", "Printing Nickname on chatRoom adapter " + error.getMessage());
                    }
                });
                firebaseHelper.getDatabaseReference()
                        .child("ChatRooms")
                        .child(chat.getRoomId())
                        .child("messages")
                        .orderByKey()
                        .limitToLast(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ChatMessageModel lastMessage = snapshot.getValue(ChatMessageModel.class);
                                    if (lastMessage != null) {
                                        holder.lastMessage.setText(lastMessage.getMessage());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase RealmTime Database error", "Printing lastMessage on chatRoom adapter " + error.getMessage());
                            }
                        });
            }
        }
    }

    private String findOtherUserId(@Nullable Map<String, Boolean> participants, String currentUserId) {
        if (participants != null) {
            for (String userId : participants.keySet()) {
                if (!userId.equals(currentUserId)) {
                    return userId;
                }
            }
        }
        return null;
    }

    private void setLoading(boolean isLoading, ViewHolder holder) {
        holder.chatroomProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        holder.chatRoomLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView username;
        private final AppCompatTextView lastMessage;
        private final CircleImageView userAvatar;
        private final LinearLayoutCompat chatRoomLayout;
        private final ProgressBar chatroomProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            chatRoomLayout = itemView.findViewById(R.id.chatRoomLayout);
            chatroomProgressBar = itemView.findViewById(R.id.chatroomProgressBar);
        }
    }
}
