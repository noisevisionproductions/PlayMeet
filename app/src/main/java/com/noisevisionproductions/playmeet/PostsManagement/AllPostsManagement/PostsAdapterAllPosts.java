package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapterAllPosts extends RecyclerView.Adapter<PostsAdapterAllPosts.MyViewHolder> {

    private final List<PostCreating> listOfPostCreating;
    private final FragmentManager fragmentManager;
    private final Context context;

    public PostsAdapterAllPosts(List<PostCreating> listOfPostCreating, FragmentManager fragmentManager, Context context) {
        this.listOfPostCreating = listOfPostCreating;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PostCreating postCreating = listOfPostCreating.get(position);
        String userId = postCreating.getUserId();
        setUserAvatar(holder, userId, context);

        holder.sportNames.setText(postCreating.getSportType());
        holder.cityNames.setText(postCreating.getCityName());
        holder.skillLevel.setText(postCreating.getSkillLevel());
        holder.addInfo.setText(postCreating.getAdditionalInfo());
        if (postCreating.getHowManyPeopleNeeded() > 0) {
            holder.numberOfPeople.setText(postCreating.getPeopleStatus());
        }

        holder.layoutOfPost.startAnimation(holder.postAnimation);

        // po kliknieciu w post, otwiera wiecej informacji o nim
        holder.layoutOfPost.setOnClickListener(v -> ButtonHelperAllPosts.handleMoreInfoButton(fragmentManager, postCreating, data -> {
        }, context));
    }

    @NonNull
    @Override
    public PostsAdapterAllPosts.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_all_content, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return listOfPostCreating.size();
    }

    private void setUserAvatar(MyViewHolder holder, String userId, Context context) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserAvatar(context, userId, holder.userAvatar);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView userAvatar;
        private final AppCompatTextView sportNames, cityNames, skillLevel, addInfo, numberOfPeople;
        private final CardView layoutOfPost;
        private final Animation postAnimation;

        public MyViewHolder(View v) {
            super(v);
            userAvatar = v.findViewById(R.id.userAvatar);
            sportNames = v.findViewById(R.id.sportNames);
            cityNames = v.findViewById(R.id.chosenCity);
            skillLevel = v.findViewById(R.id.skillLevel);
            addInfo = v.findViewById(R.id.addInfoPost);
            numberOfPeople = v.findViewById(R.id.numberOfPeople);
            layoutOfPost = v.findViewById(R.id.layoutOfPost);

            postAnimation = AnimationUtils.loadAnimation(layoutOfPost.getContext(), R.anim.post_loading_animation);
            layoutOfPost.setAnimation(postAnimation);

            setPostAnimation();
        }

        private void setPostAnimation() {
            layoutOfPost.setOnHoverListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                    view.animate()
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .setDuration(300)
                            .start();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(300)
                            .start();
                }
                return false;
            });
        }
    }
}
