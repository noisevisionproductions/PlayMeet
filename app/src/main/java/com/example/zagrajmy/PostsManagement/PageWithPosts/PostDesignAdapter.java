package com.example.zagrajmy.PostsManagement.PageWithPosts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class PostDesignAdapter extends RecyclerView.Adapter<PostDesignAdapter.MyViewHolder> {

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextInputEditText uniquePostId;
        public TextInputEditText sportNames;
        public TextInputEditText cityNames;
        public TextInputEditText skillLevel;
        public TextInputEditText addInfo;
        public TextInputEditText chosenDate;
        public TextInputEditText chosenHour;

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
        }
    }

    private final List<PostCreating> posts;

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PostCreating postCreating = posts.get(position);

        holder.uniquePostId.setText(String.valueOf(postCreating.getUniqueId()));
        holder.sportNames.setText(postCreating.getSportType());
        holder.cityNames.setText(postCreating.getCityName());
        holder.skillLevel.setText(postCreating.getSkillLevel());
        holder.addInfo.setText(postCreating.getAdditionalInfo());
        holder.chosenDate.setText(postCreating.getDateTime());
        holder.chosenHour.setText(postCreating.getHourTime());
    }

    public PostDesignAdapter(List<PostCreating> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostDesignAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Wczytaj swój plik XML jako nowy widok
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_post_design, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }
}
