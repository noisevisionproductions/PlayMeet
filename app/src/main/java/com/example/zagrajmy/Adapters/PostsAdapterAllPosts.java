package com.example.zagrajmy.Adapters;


import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

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
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class PostsAdapterAllPosts extends RecyclerView.Adapter<PostsAdapterAllPosts.MyViewHolder> {

    private final List<PostCreating> listOfPostCreating;
    private final Context context;
    private PostCreating postCreating;
    private String currentRoomId;


    public PostsAdapterAllPosts(Context context, List<PostCreating> listOfPostCreating) {
        this.listOfPostCreating = listOfPostCreating;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        postCreating = listOfPostCreating.get(position);

        try (Realm ignored = Realm.getDefaultInstance()) {
            holder.uniquePostId.setText(String.valueOf(postCreating.getPostId()));
            holder.sportNames.setText(postCreating.getSportType());
            holder.cityNames.setText(postCreating.getCityName());
            holder.skillLevel.setText(postCreating.getSkillLevel());
            holder.addInfo.setText(postCreating.getAdditionalInfo());
            holder.chosenDate.setText(postCreating.getDateTime());
            holder.chosenHour.setText(postCreating.getHourTime());

            holder.postId = Integer.parseInt(String.valueOf(postCreating.getPostId()));
        }

        //  extraInfoContainerForAllPosts.handleExtraInfo(holder, context);

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
            App realmApp = RealmAppConfig.getApp();
            User user = realmApp.currentUser();

            if (user != null) {
                String user2 = user.getId();

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

                        //TODO alternatywa do ustawiania nickname
                        // privateChatModel.setNickNameOfUser2(user.getDisplayName());

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
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextInputEditText uniquePostId, sportNames, cityNames, skillLevel, addInfo, chosenDate, chosenHour;
        protected final ConstraintLayout arrowDownOpenMenu;
        protected final LinearLayoutCompat extraInfoContainer;
        private final CardView layoutOfPost;
        protected final AppCompatButton arrowDownOpenMenuButton, savePostButton, chatButton;
        public int postId;
        protected boolean isExtraInfoOpen = false;
        private static final int ANIMATION_DURATION = 300;
        private static final int EXPANDED_HEIGHT_DP = 300;


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

            layoutOfPost = v.findViewById(R.id.layoutOfPost);

            arrowDownOpenMenu = v.findViewById(R.id.arrowDownOpenMenu);

            extraInfoContainer = v.findViewById(R.id.extraInfoContainer);

            arrowDownOpenMenuButton = v.findViewById(R.id.arrowDownOpenMenuButton);

            savePostButton = v.findViewById(R.id.savePostButton);

            chatButton = v.findViewById(R.id.chatButton);

            extendForMoreInformation();
            savePostButtonLogic();
        }

        public void extendForMoreInformation() {
            arrowDownOpenMenu.setOnClickListener(v -> {
                if (isExtraInfoOpen) {
                    animateLayoutAndExtraInfo(240, 0);
                    extraInfoContainer.setVisibility(View.GONE);
                    arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);

                    isExtraInfoOpen = false;
                } else {
                    animateLayoutAndExtraInfo(300, dpToPx(v.getContext(), EXPANDED_HEIGHT_DP));
                    arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);

                    extraInfoContainer.setVisibility(View.VISIBLE);
                    isExtraInfoOpen = true;
                }
            });
        }

        private void animateLayoutAndExtraInfo(int targetHeightDP, int extraInfoHeight) {

            ValueAnimator layoutAnimator = ValueAnimator.ofInt(layoutOfPost.getHeight(), dpToPx(layoutOfPost.getContext(), targetHeightDP));
            layoutAnimator.addUpdateListener(animator -> {
                int val = (Integer) animator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = layoutOfPost.getLayoutParams();
                layoutParams.height = val;
                layoutOfPost.setLayoutParams(layoutParams);
            });

            ValueAnimator extraInfoAnimator = ValueAnimator.ofInt(extraInfoContainer.getHeight(), extraInfoHeight);
            extraInfoAnimator.addUpdateListener(animation -> {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = extraInfoContainer.getLayoutParams();
                layoutParams.height = val;
                extraInfoContainer.setLayoutParams(layoutParams);
            });

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(layoutAnimator, extraInfoAnimator);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(ANIMATION_DURATION);
            animatorSet.start();

        }

        public static int dpToPx(Context context, float dp) {
            float density = context.getResources().getDisplayMetrics().density;
            return Math.round(dp * density);
        }

        public void savePostButtonLogic() {
            App realmApp = RealmAppConfig.getApp();
            User user = realmApp.currentUser();

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
                                newPost.setUserId(user.getId());
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
