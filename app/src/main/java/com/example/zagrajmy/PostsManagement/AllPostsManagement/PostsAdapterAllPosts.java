package com.example.zagrajmy.PostsManagement.AllPostsManagement;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import io.realm.Realm;

public class PostsAdapterAllPosts extends RecyclerView.Adapter<PostsAdapterAllPosts.MyViewHolder> {

    private final List<PostCreating> listOfPostCreating;
    private final FragmentManager fragmentManager;

    public PostsAdapterAllPosts(List<PostCreating> listOfPostCreating, FragmentManager fragmentManager) {
        this.listOfPostCreating = listOfPostCreating;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PostCreating postCreating = listOfPostCreating.get(position);

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

        holder.arrowDownOpenMenuButton.setOnClickListener(v -> ButtonHelperAllPosts.handleMoreInfoButton(fragmentManager, postCreating, postCreating.getUserId(), data -> {
        }));
        holder.arrowDownOpenMenu.setOnClickListener(v -> ButtonHelperAllPosts.handleMoreInfoButton(fragmentManager, postCreating, postCreating.getUserId(), data -> {
        }));
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextInputEditText uniquePostId, sportNames, cityNames, skillLevel, addInfo, chosenDate, chosenHour;
        protected final ConstraintLayout arrowDownOpenMenu;
        protected final LinearLayoutCompat extraInfoContainer;
        final CardView layoutOfPost;
        protected final AppCompatButton arrowDownOpenMenuButton, savePostButton, chatButton;
        public int postId;

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

        }
    }

}
