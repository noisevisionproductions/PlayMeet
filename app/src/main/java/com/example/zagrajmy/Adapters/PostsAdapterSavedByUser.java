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
import com.example.zagrajmy.PostCreatingCopy;
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class PostsAdapterSavedByUser extends RecyclerView.Adapter<PostsAdapterSavedByUser.MyViewHolder> {
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
    private final List<PostCreatingCopy> listOfPostCreatingCopy;
    private final Context context;


    public PostsAdapterSavedByUser(Context context, List<PostCreatingCopy> listOfPostCreatingCopy) {
        this.listOfPostCreatingCopy = listOfPostCreatingCopy;
        this.context = context;
        realmDatabaseManagement = RealmDatabaseManagement.getInstance();
    }

    public void extraInfo(PostsAdapterSavedByUser.MyViewHolder holder) {
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

    public void deletePostButton(PostsAdapterSavedByUser.MyViewHolder holder, int position) {

        PostCreatingCopy postCreatingCopy = listOfPostCreatingCopy.get(position);

        holder.deletePost.setText("Wypisz się");


        holder.deletePost.setOnClickListener(v -> {
            String postId = postCreatingCopy.getPostUuid();
            realmDatabaseManagement.removeUserFromPost(postId);
            listOfPostCreatingCopy.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listOfPostCreatingCopy.size());
        });
    }

    @NonNull
    @Override
    public PostsAdapterSavedByUser.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_user_activity, parent, false);
        return new PostsAdapterSavedByUser.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapterSavedByUser.MyViewHolder holder, int position) {
        PostCreatingCopy posts = listOfPostCreatingCopy.get(position);

        holder.uniquePostId.setText(String.valueOf(posts.getPostId()));
        holder.sportNames.setText(posts.getSportType());
        holder.cityNames.setText(posts.getCityName());
        holder.skillLevel.setText(posts.getSkillLevel());
        holder.addInfo.setText(posts.getAdditionalInfo());
        holder.chosenDate.setText(posts.getDateTime());
        holder.chosenHour.setText(posts.getHourTime());

        extraInfo(holder);
        deletePostButton(holder, position);
    }

    @Override
    public int getItemCount() {
        if (listOfPostCreatingCopy != null) {
            return listOfPostCreatingCopy.size();
        } else {
            return 0;
        }
    }

}