package com.example.zagrajmy.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
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
import com.example.zagrajmy.Chat.ChatMessageModel;
import com.example.zagrajmy.Chat.PrivateChatModel;
import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class PostDesignAdapterForAllPosts extends RecyclerView.Adapter<PostDesignAdapterForAllPosts.MyViewHolder> {


    private final List<PostCreating> listOfPostCreating;
    private final Context context;
    private PostCreating postCreating;

    public PostDesignAdapterForAllPosts(Context context, List<PostCreating> listOfPostCreating) {
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

        extraInfo(holder);
        chatButtonLogic(holder);
    }

    public void chatButtonLogic(MyViewHolder holder) {

        holder.chatButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String userIdThatCreatedPost = postCreating.getUserId();
            assert user != null;
            String user2 = user.getUid();

            try (Realm realm = Realm.getDefaultInstance()) {
                postCreating = realm.where(PostCreating.class).findFirst();

                // adding messages from certain users to new list
                RealmList<ChatMessageModel> chatMessageList = new RealmList<>();
                RealmResults<ChatMessageModel> chatMessageResults = realm.where(ChatMessageModel.class)
                        .equalTo("users.userId", user2)
                        .findAll();
                chatMessageList.addAll(chatMessageResults);

                PrivateChatModel existingChatRoom = realm.where(PrivateChatModel.class)
                        .beginGroup()
                        .equalTo("userIdThatCreatedPost", userIdThatCreatedPost)
                        .equalTo("user2", user2)
                        .endGroup()
                        .findFirst();

                PrivateChatModel privateChatModel;

                // checking if room already exist
                if (existingChatRoom != null) {
                    privateChatModel = existingChatRoom;
                } else {
                    privateChatModel = new PrivateChatModel();
                    privateChatModel.setUserIdThatCreatedPost(userIdThatCreatedPost);

                    privateChatModel.setUser2(user.getUid());
                    privateChatModel.setNickNameOfUser2(user.getDisplayName());
                    privateChatModel.setMessages(chatMessageList);
                }

                RealmDatabaseManagement realmDatabaseManagement = RealmDatabaseManagement.getInstance();
                realmDatabaseManagement.createChatroomInDatabase(privateChatModel);

                realm.executeTransactionAsync(realm1 -> {
                }, () -> {
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
                    v.getContext().startActivity(intent);
                }, error -> Log.e("Realm Transaction Error", Objects.requireNonNull(error.getMessage())));
            }
        });
    }

    //logika rozwijanego menu, dodatkowych informacji
    public void extraInfo(MyViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.cardView.getLayoutParams();
        holder.arrowDownOpenMenu.setOnClickListener(v -> {
            if (holder.extraInfoContainer.getVisibility() == View.GONE) {
                holder.extraInfoContainer.setVisibility(View.VISIBLE);
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, context.getResources().getDisplayMetrics());
                holder.cardView.requestLayout();
                holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
            } else {
                holder.extraInfoContainer.setVisibility(View.GONE);
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, context.getResources().getDisplayMetrics());
                holder.cardView.requestLayout();
                holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
            }
        });
        holder.arrowDownOpenMenuButton.setOnClickListener(v -> {
            if (holder.extraInfoContainer.getVisibility() == View.GONE) {
                holder.extraInfoContainer.setVisibility(View.VISIBLE);
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, context.getResources().getDisplayMetrics());
                holder.cardView.requestLayout();
                holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
            } else {
                holder.extraInfoContainer.setVisibility(View.GONE);
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, context.getResources().getDisplayMetrics());
                holder.cardView.requestLayout();
                holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
            }
        });

    }


    @NonNull
    @Override
    public PostDesignAdapterForAllPosts.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextInputEditText uniquePostId, sportNames, cityNames, skillLevel, addInfo, chosenDate, chosenHour;
        private final CardView cardView;
        private final ConstraintLayout arrowDownOpenMenu;
        private final LinearLayoutCompat extraInfoContainer;
        private final AppCompatButton arrowDownOpenMenuButton, savePostButton, chatButton;
        private PostCreating postCreating;
        private RealmDatabaseManagement realmDatabaseManagement;
        private FirebaseUser firebaseUser;

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


        public void setPostsSavedByUser(PostCreating postCreating) {
            this.postCreating = postCreating;
        }

        public void savePostButtonLogic() {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            savePostButton.setOnClickListener(v1 -> {
                realmDatabaseManagement = RealmDatabaseManagement.getInstance();
                realmDatabaseManagement.findPostCreatedByUser();

                this.postCreating = new PostCreating();
                assert firebaseUser != null;
                this.postCreating.setUserId(firebaseUser.getUid());
                assert postCreating != null;
                this.postCreating.setPostId(postCreating.getPostId());
                this.postCreating.setSportType(postCreating.getSportType());
                this.postCreating.setCityName(postCreating.getCityName());
                this.postCreating.setDateTime(postCreating.getDateTime());
                this.postCreating.setHourTime(postCreating.getHourTime());
                this.postCreating.setSkillLevel(postCreating.getSkillLevel());
                this.postCreating.setAdditionalInfo(postCreating.getAdditionalInfo());
                this.postCreating.setPostSavedByUser(true);
          /*      this.postCreating.setButtonColorAndText(String.valueOf(Color.BLACK), "Zapisałeś się!");
                this.postCreating.setIsPostSavedByUser(true);*/

                realmDatabaseManagement.savePostToDatabaseAsSignedIn(this.postCreating);

                savePostButton.setBackgroundColor(Color.BLACK);
                savePostButton.setText("Zapisałeś się!");
            });
        }
    }

}
