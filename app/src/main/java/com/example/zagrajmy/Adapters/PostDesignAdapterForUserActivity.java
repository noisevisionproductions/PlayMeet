package com.example.zagrajmy.Adapters;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.realm.Realm;

public class PostDesignAdapterForUserActivity extends RecyclerView.Adapter<PostDesignAdapterForUserActivity.MyViewHolder> {
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
        private final AppCompatButton deletePost;
        private PostCreating postCreating;
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

            deletePost = v.findViewById(R.id.deletePost);
        }
    }


    private final RealmDatabaseManagement realmDatabaseManagement;
    private final List<PostCreating> listOfPostCreating;
    private final Context context;

    public PostDesignAdapterForUserActivity(Context context, List<PostCreating> listOfPostCreating) {
        this.listOfPostCreating = listOfPostCreating;
        this.context = context;
        realmDatabaseManagement = RealmDatabaseManagement.getInstance();
    }

    public void extraInfo(PostDesignAdapterForUserActivity.MyViewHolder holder) {
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

    public void deletePostButton(PostDesignAdapterForUserActivity.MyViewHolder holder, int position) {
        PostCreating postCreating = listOfPostCreating.get(position);

        if (postCreating.isPostSavedByUser()) {
            holder.deletePost.setText("Wypisz się");
        }

        holder.deletePost.setOnClickListener(v -> {
            int postId = postCreating.getPostId();
            realmDatabaseManagement.deletePost(postId);
            listOfPostCreating.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listOfPostCreating.size());
        });
    }

    @NonNull
    @Override
    public PostDesignAdapterForUserActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_user_activity, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostDesignAdapterForUserActivity.MyViewHolder holder, int position) {

        PostCreating postCreating = listOfPostCreating.get(position);

        // bind data from the posts list
        holder.uniquePostId.setText(String.valueOf(postCreating.getPostId()));
        holder.sportNames.setText(postCreating.getSportType());
        holder.cityNames.setText(postCreating.getCityName());
        holder.skillLevel.setText(postCreating.getSkillLevel());
        holder.addInfo.setText(postCreating.getAdditionalInfo());
        holder.chosenDate.setText(postCreating.getDateTime());
        holder.chosenHour.setText(postCreating.getHourTime());

        extraInfo(holder);
        deletePostButton(holder, position);
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