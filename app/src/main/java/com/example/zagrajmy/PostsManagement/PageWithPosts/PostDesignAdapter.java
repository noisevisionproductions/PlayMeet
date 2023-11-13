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
        // Tutaj zdefiniuj pola dla elementów widoku, które chcesz aktualizować.
        // Na przykład, jeśli masz TextView w swoim pliku XML, możesz dodać:
        // public TextView myTextView;
        public TextInputEditText uniquePostId;
        public TextInputEditText sportNames;
        public TextInputEditText cityNames;
        public TextInputEditText skillLevel;
        public TextInputEditText addInfo;

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
            // Tutaj zainicjalizuj swoje widoki. Na przykład:
            // myTextView = v.findViewById(R.id.my_text_view);
        }
    }

    private final List<PostCreating> posts;

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Aktualizuj widoki w holderze danymi z listy
        // Na przykład:
        // holder.myTextView.setText(posts.get(position).getText());
        PostCreating postCreating = posts.get(position);

        holder.uniquePostId.setText(String.valueOf(postCreating.getUniqueId()));
        holder.sportNames.setText(postCreating.getSportType());
        holder.cityNames.setText(postCreating.getCityName());
        holder.skillLevel.setText(postCreating.getSkillLevel());
        holder.addInfo.setText(postCreating.getAdditionalInfo());
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
