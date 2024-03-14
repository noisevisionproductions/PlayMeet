package com.noisevisionproductions.playmeet.postsManagement.userPosts;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirebaseUserRepository;
import com.noisevisionproductions.playmeet.firebase.FirestorePostRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.ViewHolderUpdater;
import com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.bottomSheetFragment.ButtonsForChatAndSignIn;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSavedByUserPosts extends RecyclerView.Adapter<AdapterSavedByUserPosts.MyViewHolder> {
    private final List<PostModel> listOfRegisteredPosts;
    private final Context context;
    private final FragmentManager fragmentManager;
    private final AppCompatTextView noPostInfo;


    public AdapterSavedByUserPosts(Context context, FragmentManager fragmentManager, List<PostModel> listOfRegisteredPosts, AppCompatTextView noPostInfo) {
        this.listOfRegisteredPosts = listOfRegisteredPosts;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.noPostInfo = noPostInfo;
    }

    @NonNull
    @Override
    public AdapterSavedByUserPosts.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_user_activity, parent, false);
        PostHelperSignedUpUser.makePostSmaller(v, parent, listOfRegisteredPosts);
        return new AdapterSavedByUserPosts.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSavedByUserPosts.MyViewHolder holder, int position) {
        holder.applyAnimation();
        PostModel posts = listOfRegisteredPosts.get(position);
        String userId = posts.getUserId();
        FirebaseHelper firebaseHelper = new FirebaseHelper();

        PostHelperSignedUpUser.getPeopleStatus(posts.getPostId(), holder);

        if (firebaseHelper.getCurrentUser() != null) {
            createDialog(holder, position, firebaseHelper.getCurrentUser().getUid(), posts.getPostId());
        }

        holder.sportNames.setText(posts.getSportType());
        holder.cityNames.setText(posts.getCityName());
        holder.setUserAvatar(context, userId);

        holder.layoutOfPost.setOnClickListener(v -> ButtonsForChatAndSignIn.handleMoreInfoButton(fragmentManager, posts, context));
        holder.chatButton.setOnClickListener(v -> ButtonsForChatAndSignIn.handleChatButtonClick(v, posts.getUserId(), fragmentManager));
    }

    @Override
    public int getItemCount() {
        return listOfRegisteredPosts.size();
    }

    private void createDialog(MyViewHolder holder, int position, String currentUserId, String postId) {
        holder.deletePost.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setMessage("Czy na pewno chcesz usunąć ten post?")
                .setPositiveButton("Tak", (dialog, which) -> {
                    removeRegistration(postId, currentUserId);
                    listOfRegisteredPosts.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, listOfRegisteredPosts.size());
                    if (listOfRegisteredPosts.isEmpty()) {
                        new Handler().postDelayed(() -> noPostInfo.setVisibility(View.VISIBLE), 100);
                    }
                })
                .setNegativeButton("Nie", null).show());
    }

    private void removeRegistration(String postId, String userId) {
        FirestorePostRepository firestorePostRepository = new FirestorePostRepository();
        firestorePostRepository.removeUserFromRegistration(postId, userId, new OnCompletionListener() {
            @Override
            public void onSuccess() {
                decrementJoinedPostsCount(userId);
                ToastManager.showToast(context, "Zostałeś wypisany!");
            }

            @Override
            public void onFailure(Exception e) {
                ToastManager.showToast(context, "Błąd podczas usuwania rejestracji: " + e.getMessage());
                Log.e("Firebase Update Error", "Removing signed up user when saved post is removed " + e.getMessage());
            }
        });
    }

    private void decrementJoinedPostsCount(String userId) {
        FirebaseUserRepository firebaseUserRepository = new FirebaseUserRepository();
        firebaseUserRepository.decrementJoinedPostsCount(userId, new OnCompletionListener() {
            @Override
            public void onSuccess() {
                Log.d("User joined posts updated", "User joined posts updated");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("User joined posts error ", "User joined posts error " + e.getMessage());
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements ViewHolderUpdater {
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