package com.example.zagrajmy.PostsManagement.PageWithPosts;


import android.content.Context;
import android.graphics.Color;
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

import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostsManagement.UserPosts.PostsSavedByUser;
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.realm.Realm;

public class PostDesignAdapter extends RecyclerView.Adapter<PostDesignAdapter.MyViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextInputEditText uniquePostId;
        private final TextInputEditText sportNames;
        private final TextInputEditText cityNames;
        private final TextInputEditText skillLevel;
        private final TextInputEditText addInfo;
        private final TextInputEditText chosenDate;
        private final TextInputEditText chosenHour;
        private final CardView cardView;
        private final ConstraintLayout arrowDownOpenMenu;
        private final LinearLayoutCompat extraInfoContainer;
        private final AppCompatButton arrowDownOpenMenuButton;
        private final AppCompatButton savePostButton;
        private PostsSavedByUser postsSavedByUser;
        private Realm realm;
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


            savePostButtonLogic();
        }

        public void setPostsSavedByUser(PostsSavedByUser postsSavedByUser) {
            this.postsSavedByUser = postsSavedByUser;
        }

        public void savePostButtonLogic() {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            savePostButton.setOnClickListener(v1 -> {
                realm = Realm.getDefaultInstance();
                PostCreating postCreating = realm.where(PostCreating.class).equalTo("isCreatedByUser", true).findFirst();


                postsSavedByUser = new PostsSavedByUser();
                assert firebaseUser != null;
                postsSavedByUser.setUserId(firebaseUser.getUid());
                assert postCreating != null;
                postsSavedByUser.setPostId(postCreating.getPostId());
                postsSavedByUser.setSportType(postCreating.getSportType());
                postsSavedByUser.setCityName(postCreating.getCityName());
                postsSavedByUser.setDateTime(postCreating.getDateTime());
                postsSavedByUser.setHourTime(postCreating.getHourTime());
                postsSavedByUser.setSkillLevel(postCreating.getSkillLevel());
                postsSavedByUser.setAdditionalInfo(postCreating.getAdditionalInfo());
                postsSavedByUser.setButtonColorAndText(String.valueOf(Color.BLACK), "Zapisałeś się!");
                postsSavedByUser.setIsPostSavedByUser(true);
                setPostsSavedByUser(postsSavedByUser);

                RealmDatabaseManagement.getInstance().savePostToDatabaseAsSignedIn(postsSavedByUser);

                savePostButton.setBackgroundColor(Color.BLACK);
                savePostButton.setText("Zapisałeś się!");
            });
        }

    }

    private final List<PostCreating> posts;
    private final List<PostsSavedByUser> postsSavedByUsers;
    private final Context context;


    public PostDesignAdapter(Context context, List<PostCreating> posts, List<PostsSavedByUser> postsSavedByUsers) {
        this.posts = posts;
        this.postsSavedByUsers = postsSavedByUsers;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //  Realm realm = Realm.getDefaultInstance();

        if (posts != null) {
            // bind data from the posts list
            PostCreating postCreating = posts.get(position);
            holder.uniquePostId.setText(String.valueOf(postCreating.getPostId()));
            holder.sportNames.setText(postCreating.getSportType());
            holder.cityNames.setText(postCreating.getCityName());
            holder.skillLevel.setText(postCreating.getSkillLevel());
            holder.addInfo.setText(postCreating.getAdditionalInfo());
            holder.chosenDate.setText(postCreating.getDateTime());
            holder.chosenHour.setText(postCreating.getHourTime());

            // ...
        } else if (postsSavedByUsers != null) {
            // bind data from the postsSavedByUsers list
            PostsSavedByUser userPost = postsSavedByUsers.get(position);
            String buttonColor = userPost.getButtonColor();
            String buttonText = userPost.getButtonText();

            if (buttonColor != null && !buttonColor.isEmpty()) {
                holder.savePostButton.setBackgroundColor(Color.BLACK);
                holder.savePostButton.setFocusable(false);
            }

            if (buttonText != null && !buttonText.isEmpty()) {
                holder.savePostButton.setText(buttonText);
                holder.savePostButton.setFocusable(false);
            }

            holder.uniquePostId.setText(String.valueOf(userPost.getPostId()));
            holder.sportNames.setText(userPost.getSportType());
            holder.cityNames.setText(userPost.getCityName());
            holder.skillLevel.setText(userPost.getSkillLevel());
            holder.addInfo.setText(userPost.getAdditionalInfo());
            holder.chosenDate.setText(userPost.getDateTime());
            holder.chosenHour.setText(userPost.getHourTime());

        }
        extraInfo(holder);
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
    public PostDesignAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Wczytaj swój plik XML jako nowy widok
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_all_content, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        if (posts != null) {
            return posts.size();
        } else if (postsSavedByUsers != null) {
            return postsSavedByUsers.size();
        } else {
            return 0;
        }
    }
}
