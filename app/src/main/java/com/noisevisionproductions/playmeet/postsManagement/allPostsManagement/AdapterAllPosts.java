package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAllPosts extends FirebaseRecyclerAdapter<PostCreating, AdapterAllPosts.MyViewHolder> {

    private final FragmentManager fragmentManager;
    private final Context context;

    public AdapterAllPosts(FirebaseRecyclerOptions<PostCreating> options, FragmentManager fragmentManager, Context context) {
        super(options);
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int i, @NonNull PostCreating postCreating) {
        AdapterAllPostsManagement.setPostAnimation(holder);

        String userId = postCreating.getUserId();
        AdapterAllPostsManagement.setUserAvatar(holder, userId, context);

        AdapterAllPostsManagement.getSkillLevel(postCreating, holder);
        AdapterAllPostsManagement.reportPost(holder, postCreating.getPostId(), context);

        AdapterAllPostsManagement.getPeopleStatus(postCreating.getPostId(), holder);

        holder.sportNames.setText(postCreating.getSportType());
        holder.cityNames.setText(postCreating.getCityName());
        holder.addInfo.setText(postCreating.getAdditionalInfo());

        // po kliknieciu w post, otwiera wiecej informacji o nim
        if (FirebaseAuthManager.isUserLoggedIn()) {
            holder.layoutOfPost.setOnClickListener(v -> ButtonsForChatAndSignIn.handleMoreInfoButton(fragmentManager, postCreating, context));
        } else {
            ProjectUtils.showLoginSnackBar(context);
        }
    }

    @NonNull
    @Override
    public AdapterAllPosts.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_all_content, parent, false);
        return new MyViewHolder(v);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        protected final CircleImageView userAvatar;
        private final AppCompatTextView sportNames;
        private final AppCompatTextView cityNames;
        private final AppCompatTextView addInfo;
        protected final AppCompatTextView numberOfPeople;
        protected final AppCompatImageView skillLevel;
        protected final AppCompatImageView overflowIcon;
        protected final CardView layoutOfPost;

        public MyViewHolder(@NonNull View v) {
            super(v);
            userAvatar = v.findViewById(R.id.userAvatar);
            sportNames = v.findViewById(R.id.sportNames);
            cityNames = v.findViewById(R.id.chosenCity);
            skillLevel = v.findViewById(R.id.skillLevel);
            addInfo = v.findViewById(R.id.addInfoPost);
            numberOfPeople = v.findViewById(R.id.numberOfPeople);
            layoutOfPost = v.findViewById(R.id.layoutOfPost);
            overflowIcon = v.findViewById(R.id.overflowIcon);
        }
    }
}
