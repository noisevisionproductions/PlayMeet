package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.interfaces.ViewHolderUpdater;
import com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.BottomSheetFragment.ButtonsForChatAndSignIn;
import com.noisevisionproductions.playmeet.postsManagement.userPosts.PostHelperSignedUpUser;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAllPosts extends FirestorePagingAdapter<PostModel, AdapterAllPosts.MyViewHolder> {
    private final FragmentManager fragmentManager;
    private final Context context;

    public AdapterAllPosts(@NonNull FirestorePagingOptions<PostModel> options, FragmentManager fragmentManager, Context context) {
        super(options);
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int i, @NonNull PostModel postModel) {
        holder.applyAnimation();
        String userId = postModel.getUserId();

        AdapterAllPostsHelper.getSkillLevel(postModel, holder);
        AdapterAllPostsHelper.reportPost(holder, postModel.getPostId(), context);

        PostHelperSignedUpUser.getPeopleStatus(postModel.getPostId(), holder);

        holder.setUserAvatar(context, userId);
        holder.sportNames.setText(postModel.getSportType());
        holder.cityNames.setText(postModel.getCityName());
        holder.addInfo.setText(postModel.getAdditionalInfo());

        // po kliknieciu w post, otwiera wiecej informacji o nim
        if (FirebaseAuthManager.isUserLoggedIn()) {
            holder.layoutOfPost.setOnClickListener(v -> ButtonsForChatAndSignIn.handleMoreInfoButton(fragmentManager, postModel, context));
        } else {
            ProjectUtils.showLoginSnackBar(context);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_all_content, parent, false);
        return new MyViewHolder(v);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements ViewHolderUpdater {
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

        @Override
        public void updatePeopleStatus(String status) {
            this.numberOfPeople.setText(status);
        }

        @Override
        public void applyAnimation() {
            Animation postAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.post_loading_animation);
            itemView.setOnHoverListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                    view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                }
                return false;
            });
            itemView.setAnimation(postAnimation);
            itemView.startAnimation(postAnimation);
        }

        @Override
        public void setUserAvatar(Context context, String userId) {
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.getUserAvatar(context, userId, this.userAvatar);
        }
    }
}
