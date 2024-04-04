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
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter for chat rooms, that uses {@link FirebaseRecyclerAdapter}.
 */
public class ListOfChatRoomsAdapter extends FirebaseRecyclerAdapter<ChatRoomModel, ListOfChatRoomsAdapter.ViewHolder> {
    private final OnItemClickListener listener;
    private final Context context;


    /**
     * Interface defining behavior when clicking on a chat room list layout.
     */
    public interface OnItemClickListener {
        void onItemClick(ChatRoomModel chatRoomModel);
    }

    public ListOfChatRoomsAdapter(OnItemClickListener listener, @NonNull FirebaseRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.listener = listener;
        this.context = context;
    }

    /**
     * Assigns chat room model data to views in the ViewHolder.
     * The method is called by RecyclerView to display the data at a given position.
     *
     * @param holder   ViewHolder, which should be updated with model data.
     * @param position Element position in adapter data bundle.
     * @param model    Data model with chat rooms.
     */
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ChatRoomModel model) {
        getChatRoomData(holder, model);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chatroom_design, parent, false);
        return new ViewHolder(view);
    }

    /**
     * First, im calling the method setLoading in order to show loading indicator inside the chat room layout.
     * Then, I'm making a Map with chat participants, so I can get the last message from the chat and nickname from the user that current user speak with.
     *
     * @param holder ViewHolder of chat room, that needs to be updated.
     * @param chat   Chat room model that contains data to show.
     */
    private void getChatRoomData(@NonNull ViewHolder holder, @NonNull final ChatRoomModel chat) {
        setLoading(true, holder);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            String currentUserId = firebaseHelper.getCurrentUser().getUid();

            Map<String, Boolean> participants = chat.getParticipants();

            String otherUserId = findOtherUserId(participants, currentUserId);

            /*
             * On chosen chat room click, I'm calling an interface onItemClick,
             * in order to transfer current user to the correct chat room with messages.
             * */
            holder.itemView.setOnClickListener(v -> listener.onItemClick(chat));

            if (otherUserId != null) {
                /*
                 * Creating a counter with tasks which need to be completed before hiding progress bar.
                 * Current number of tasks is 2 (for nickname and message).
                 * */
                final AtomicInteger completedTasks = new AtomicInteger(0);
                final int totalTasks = 2;

                firebaseHelper.getUserAvatar(context, otherUserId, holder.userAvatar);
                firebaseHelper.getUserNickName(otherUserId, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String nickname = snapshot.getValue(String.class);
                            if (nickname != null) {
                                holder.username.setText(nickname);
                            }
                        } else {
                            holder.username.setText(context.getString(R.string.noNickname));
                        }
                        /*
                         * If task is completed, I increment the number of total tasks by 1.
                         * */
                        if (completedTasks.incrementAndGet() == totalTasks) {
                            setLoading(false, holder);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase RealmTime Database error", "Printing Nickname on chatRoom adapter " + error.getMessage());
                        if (completedTasks.incrementAndGet() == totalTasks) {
                            setLoading(false, holder);
                        }
                    }
                });
                /*
                 * Querying last message from given chat room in order to get it from DB and show in app.
                 * */
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
                                    /*
                                     * If message exists, before showing it on the chat room layout, I'm making sure that
                                     * the length of the message has no more than 30 characters.
                                     * If it does, then after 30th character, I'm changing everything after that into "...".
                                     * */
                                    if (lastMessage != null) {
                                        String fullMessage = lastMessage.getMessage();
                                        String shorterMessage = fullMessage.length() > 30 ? fullMessage.substring(0, 30) + "..." : fullMessage;
                                        holder.lastMessage.setText(shorterMessage);
                                        /*
                                         * Incrementing number of total tasks again.
                                         * After that data from DB should be shown in the same time from the tasks.
                                         * */
                                        if (completedTasks.incrementAndGet() == totalTasks) {
                                            setLoading(false, holder);
                                        }
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

    /**
     * I retrieving from DB all of the participants.
     * After that I'm making sure that it won't retrieve user ID from current logged user.
     *
     * @param participants  Map with chat participants, where key is user ID.
     * @param currentUserId User ID from the current logged in user.
     * @return User ID from other user that is signed up in the chat room,
     * or null, if for some reason other user is not found.
     */
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

    /**
     * Method is being used, when the data is being retrieved from DB. It shows the progress bar and hides layout of chat room,
     * in order to show the user the loading status. If data is retrieved, then it hides progress bar, and shows layout.
     */
    private void setLoading(boolean isLoading, ViewHolder holder) {
        holder.chatroomProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        holder.chatRoomLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView username, lastMessage;
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
