package com.example.zagrajmy.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Chat.ChatActivity;
import com.example.zagrajmy.Chat.PrivateChatModel;
import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostCreatingCopy;
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsAdapterAllPosts extends RecyclerView.Adapter<PostsAdapterAllPosts.MyViewHolder> {

    private final List<PostCreating> listOfPostCreating;
    private final Context context;
    private PostCreating postCreating;
    private String currentRoomId;
    private Set<Integer> openExtraInfoPostIds = new HashSet<>();


    public PostsAdapterAllPosts(Context context, List<PostCreating> listOfPostCreating) {
        this.listOfPostCreating = listOfPostCreating;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        postCreating = listOfPostCreating.get(position);

        holder.uniquePostId.setText(String.valueOf(postCreating.getPostId()));
        holder.sportNames.setText(postCreating.getSportType());
        holder.cityNames.setText(postCreating.getCityName());
        holder.skillLevel.setText(postCreating.getSkillLevel());
        holder.addInfo.setText(postCreating.getAdditionalInfo());
        holder.chosenDate.setText(postCreating.getDateTime());
        holder.chosenHour.setText(postCreating.getHourTime());

        holder.postId = Integer.parseInt(String.valueOf(postCreating.getPostId()));

        ExtraInfoContainerForAllPosts.handleExtraInfo(holder, context);
        chatButtonLogic(holder);
    }

    @NonNull
    @Override
    public PostsAdapterAllPosts.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_all_content, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        if (listOfPostCreating != null) {
            return listOfPostCreating.size();
        } else {
            return 0;
        }
    }

    public void chatButtonLogic(MyViewHolder holder) {

        holder.chatButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            assert user != null;
            String user2 = user.getUid();

            try (Realm realm = Realm.getDefaultInstance()) {

                String userIdThatCreatedPost = postCreating.getUserId();

                postCreating = realm.where(PostCreating.class).findFirst();

                PrivateChatModel existingChatRoom = realm.where(PrivateChatModel.class)
                        .beginGroup()
                        .equalTo("userIdThatCreatedPost", userIdThatCreatedPost)
                        .equalTo("user2", user2)
                        .endGroup()
                        .findFirst();

                // checking if room already exist
                RealmDatabaseManagement realmDatabaseManagement = RealmDatabaseManagement.getInstance();

                if (existingChatRoom == null) {
                    PrivateChatModel privateChatModel = new PrivateChatModel();
                    privateChatModel.setUserIdThatCreatedPost(userIdThatCreatedPost);

                    privateChatModel.setUser2(user2);
                    privateChatModel.setNickNameOfUser2(user.getDisplayName());

                    currentRoomId = privateChatModel.getRoomId();

                    realmDatabaseManagement.createChatroomInDatabase(privateChatModel);
                } else {
                    currentRoomId = existingChatRoom.getRoomId();
                    realmDatabaseManagement.createChatroomInDatabase(existingChatRoom);
                }
                realm.executeTransactionAsync(realm1 -> {
                }, () -> {
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
                    intent.putExtra("roomId", currentRoomId);
                    v.getContext().startActivity(intent);
                }, error -> Log.e("Realm Transaction Error", Objects.requireNonNull(error.getMessage())));
            }
        });
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextInputEditText uniquePostId, sportNames, cityNames, skillLevel, addInfo, chosenDate, chosenHour;
        protected final CardView cardView;
        protected final ConstraintLayout arrowDownOpenMenu;
        protected final LinearLayoutCompat extraInfoContainer;
        protected final AppCompatButton arrowDownOpenMenuButton, savePostButton, chatButton;
        public int postId;
        protected boolean isExtraInfoOpen = false;


        public MyViewHolder(View v) {
            super(v);

            uniquePostId = v.findViewById(R.id.uniquePostId);
            uniquePostId.setFocusable(false);

            sportNames = v.findViewById(R.id.sportNames);
            sportNames.setFocusable(false);

            cityNames = v.findViewById(R.id.chosenCity);
            cityNames.setFocusable(false);

            skillLevel = v.findViewById(R.id.skilLevel);
            skillLevel.setFocusable(false);

            addInfo = v.findViewById(R.id.addInfoPost);
            addInfo.setFocusable(false);

            chosenDate = v.findViewById(R.id.chosenDate);
            chosenDate.setFocusable(false);

            chosenHour = v.findViewById(R.id.chosenHour);
            chosenHour.setFocusable(false);

            arrowDownOpenMenu = v.findViewById(R.id.arrowDownOpenMenu);

            extraInfoContainer = v.findViewById(R.id.extraInfoContainer);

            cardView = v.findViewById(R.id.layoutOfPost);

            arrowDownOpenMenuButton = v.findViewById(R.id.arrowDownOpenMenuButton);

            savePostButton = v.findViewById(R.id.savePostButton);

            chatButton = v.findViewById(R.id.chatButton);

            savePostButtonLogic();
        }

        public void savePostButtonLogic() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            savePostButton.setOnClickListener(v1 -> {
                try (Realm realm = Realm.getDefaultInstance()) {
                    realm.executeTransactionAsync(realm1 -> {

                        PostCreating clickedPost = realm1.where(PostCreating.class)
                                .equalTo("postId", postId)
                                .findFirst();
                        if (clickedPost != null && user != null) {
                            PostCreatingCopy existingPost = realm1.where(PostCreatingCopy.class)
                                    .equalTo("postId", postId)
                                    .findFirst();

                            if (existingPost == null) {
                                PostCreatingCopy newPost = new PostCreatingCopy();
                                newPost.setUserId(user.getUid());
                                newPost.setPostId(clickedPost.getPostId());
                                newPost.setSportType(clickedPost.getSportType());
                                newPost.setCityName(clickedPost.getCityName());
                                newPost.setDateTime(clickedPost.getDateTime());
                                newPost.setHourTime(clickedPost.getHourTime());
                                newPost.setSkillLevel(clickedPost.getSkillLevel());
                                newPost.setAdditionalInfo(clickedPost.getAdditionalInfo());
                                newPost.setSavedByUser(true);

                                realm1.insertOrUpdate(newPost);
                            }
                        }

                        savePostButton.setBackgroundColor(Color.BLACK);
                        savePostButton.setText(R.string.postSavedInfo);


                    }, () -> {

                    }, error -> Log.e("Realm Transaction Error", Objects.requireNonNull(error.getMessage())));
                }
            });
        }
    }

}
