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
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PostDesignAdapterForAllPosts extends RecyclerView.Adapter<PostDesignAdapterForAllPosts.MyViewHolder> {

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

    private final List<PostCreating> listOfPostCreating;
    private final Context context;


    public PostDesignAdapterForAllPosts(Context context, List<com.example.zagrajmy.PostCreating> listOfPostCreating) {
        this.listOfPostCreating = listOfPostCreating;
        this.context = context;
    }

 /*   public void savePostButton(PostDesignAdapterForUserActivity.MyViewHolder holder, int position) {
        holder..setOnClickListener(v -> {
            PostCreating postCreating = listOfPostCreating.get(position);
            int postId = postCreating.getPostId();
            realmDatabaseManagement.deletePost(postId);
            listOfPostCreating.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listOfPostCreating.size());
        });
    }*/

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        com.example.zagrajmy.PostCreating postCreating = listOfPostCreating.get(position);
        holder.uniquePostId.setText(String.valueOf(postCreating.getPostId()));
        holder.sportNames.setText(postCreating.getSportType());
        holder.cityNames.setText(postCreating.getCityName());
        holder.skillLevel.setText(postCreating.getSkillLevel());
        holder.addInfo.setText(postCreating.getAdditionalInfo());
        holder.chosenDate.setText(postCreating.getDateTime());
        holder.chosenHour.setText(postCreating.getHourTime());

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
    public PostDesignAdapterForAllPosts.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Wczytaj swój plik XML jako nowy widok
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
}
