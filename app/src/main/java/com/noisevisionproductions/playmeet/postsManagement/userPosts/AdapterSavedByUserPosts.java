package com.noisevisionproductions.playmeet.postsManagement.userPosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.ButtonsPostsAdapters;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSavedByUserPosts extends RecyclerView.Adapter<AdapterSavedByUserPosts.MyViewHolder> {
    private final List<PostCreatingCopy> listOfPostCreatingCopy;
    private final Context context;
    private final FragmentManager fragmentManager;
    private final AppCompatTextView noPostInfo;

    public AdapterSavedByUserPosts(Context context, FragmentManager fragmentManager, List<PostCreatingCopy> listOfPostCreatingCopy, AppCompatTextView noPostInfo) {
        this.listOfPostCreatingCopy = listOfPostCreatingCopy;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.noPostInfo = noPostInfo;
    }

    @NonNull
    @Override
    public AdapterSavedByUserPosts.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_user_activity, parent, false);
        PostHelperSignedUpUser.makePostSmaller(v, parent, listOfPostCreatingCopy);
        return new AdapterSavedByUserPosts.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSavedByUserPosts.MyViewHolder holder, int position) {
        PostCreatingCopy posts = listOfPostCreatingCopy.get(position);
        String userId = posts.getUserId();

        if (userId != null) {
            PostHelperSignedUpUser.setUserAvatar(holder, userId, context);
        }
        PostHelperSignedUpUser.getPeopleStatus(posts.getPostId(), holder);
        PostHelperSignedUpUser.deletePost(userId, posts.getPostId(), this, holder, listOfPostCreatingCopy, noPostInfo, position);

        holder.sportNames.setText(posts.getSportType());
        holder.cityNames.setText(posts.getCityName());

        holder.layoutOfPost.setOnClickListener(v -> ButtonsPostsAdapters.handleMoreInfoButton(fragmentManager, posts, context));
        holder.chatButton.setOnClickListener(v -> ButtonsPostsAdapters.handleChatButtonClick(v, posts.getUserId()));
    }

    @Override
    public int getItemCount() {
        return listOfPostCreatingCopy.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        protected final CircleImageView userAvatar;
        private final CardView layoutOfPost;
        private final AppCompatTextView sportNames;
        private final AppCompatTextView cityNames;
        protected final AppCompatTextView numberOfPeople;
        protected final AppCompatButton deletePost;
        private final AppCompatButton chatButton;

        public MyViewHolder(@NonNull View v) {
            super(v);
            layoutOfPost = v.findViewById(R.id.layoutOfPost);
            userAvatar = v.findViewById(R.id.userAvatar);
            sportNames = v.findViewById(R.id.sportNames);
            cityNames = v.findViewById(R.id.chosenCity);
            numberOfPeople = v.findViewById(R.id.numberOfPeople);
            deletePost = v.findViewById(R.id.deletePost);
            chatButton = v.findViewById(R.id.chatButtonSavedPosts);
        }
    }
}