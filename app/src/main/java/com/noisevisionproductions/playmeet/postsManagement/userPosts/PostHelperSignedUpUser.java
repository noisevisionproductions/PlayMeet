package com.noisevisionproductions.playmeet.postsManagement.userPosts;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.userManagement.UserModel;

import java.util.List;

public class PostHelperSignedUpUser {

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

    public static void makePostSmaller(View view, ViewGroup parent, List<PostModel> listOfPostCreatingCopy) {
        if (listOfPostCreatingCopy.size() > 1) {
            DisplayMetrics displayMetrics = parent.getContext().getResources().getDisplayMetrics();
            int width = (int) (displayMetrics.widthPixels * 0.9);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.width = width;
            view.setLayoutParams(layoutParams);
        }
    }

    public static void getPeopleStatus(@NonNull String postId, ViewHolderUpdater updater) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("PostCreating").document(postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String peopleStatus = documentSnapshot.getString("peopleStatus");
                            if (peopleStatus != null) {
                                updater.updatePeopleStatus(peopleStatus);
                            }
                        } else {
                            Log.e("Firestore", "No such document");
                        }
                    } else {
                        Log.e("Firestore error", "get failed with " + task.getException());
                    }
                });
    }
}