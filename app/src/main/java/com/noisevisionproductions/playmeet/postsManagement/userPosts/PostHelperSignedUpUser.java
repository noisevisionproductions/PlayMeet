package com.noisevisionproductions.playmeet.postsManagement.userPosts;

import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.userManagement.UserModel;

import java.util.List;

public class PostHelperSignedUpUser {
    public static void deletePost(AdapterSavedByUserPosts adapter, AdapterSavedByUserPosts.MyViewHolder holder, List<PostCreatingCopy> listOfPostCreatingCopy, AppCompatTextView noPostInfo, int position) {
        PostCreatingCopy postCreatingCopy = listOfPostCreatingCopy.get(position);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            String currentUserId = firebaseHelper.getCurrentUser().getUid();
            String postId = postCreatingCopy.getPostId();
            DatabaseReference savedPostCreating = FirebaseDatabase.getInstance().getReference("SavedPostCreating").child(currentUserId).child(postId);

            holder.deletePost.setText(R.string.signOutFromThePost);
            holder.deletePost.setOnClickListener(v -> savedPostCreating.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DatabaseReference postReference = FirebaseDatabase.getInstance().getReference().child("PostCreating").child(postId);
                    postReference.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            PostCreating postCreating = currentData.getValue(PostCreating.class);
                            if (postCreating != null && postCreating.getPeopleSignedUp() > 0) {
                                postCreating.deleteSignedUpUser(currentUserId);
                                postCreating.setActivityFull(false);
                                currentData.setValue(postCreating);

                                decrementJoinedPostsCount(currentUserId);
                            }
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            if (error != null) {
                                Log.e("Firebase Update Error", "Removing signed up user when saved post is removed " + error.getMessage());
                            } else {
                                listOfPostCreatingCopy.remove(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, listOfPostCreatingCopy.size());
                                if (listOfPostCreatingCopy.isEmpty()) {
                                    new Handler().postDelayed(() -> noPostInfo.setVisibility(View.VISIBLE), 100);
                                }
                            }
                        }
                    });
                } else {
                    Log.e("PostsAdapterSavedByUser", "Błąd podczas usuwania z bazy danych " + task.getException());
                }
            }));
        }
    }

    public static void decrementJoinedPostsCount(String currentUserId) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUserId);
        userReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                UserModel userModel = currentData.getValue(UserModel.class);
                if (userModel != null) {
                    userModel.decrementJoinedPostsCount();
                    currentData.setValue(userModel);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e("Firebase Update Error", "decrementing joined posts count in current user " + error.getMessage());
                }
            }
        });
    }

    public static void setUserAvatar(@NonNull AdapterSavedByUserPosts.MyViewHolder holder, @NonNull String userId, @NonNull Context context) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserAvatar(context, userId, holder.userAvatar);
    }

    public static void makePostSmaller(View view, ViewGroup parent, List<PostCreatingCopy> listOfPostCreatingCopy) {
        if (listOfPostCreatingCopy.size() > 1) {
            DisplayMetrics displayMetrics = parent.getContext().getResources().getDisplayMetrics();
            int width = (int) (displayMetrics.widthPixels * 0.9);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.width = width;
            view.setLayoutParams(layoutParams);
        }
    }

    public static void getPeopleStatus(@NonNull String postId, @NonNull AdapterSavedByUserPosts.MyViewHolder holder) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getJoinedPeopleStatus(postId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String peopleStatus = snapshot.getValue(String.class);
                    holder.numberOfPeople.setText(peopleStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase RealmTime Database error", "Showing number of people on posts in adapter " + error.getMessage());
            }
        });
    }

}