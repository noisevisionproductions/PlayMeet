package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;

import java.util.Objects;

public class SavePostHandler {
    private final View view;
    private final String postId;
    private final FirebaseHelper firebaseHelper;
    private final String currentUserId;

    public SavePostHandler(View view, String postId) {
        this.view = view;
        this.postId = postId;
        this.firebaseHelper = new FirebaseHelper();
        this.currentUserId = firebaseHelper.getCurrentUser().getUid();
    }

    public void handleSavePostButton() {
        if (firebaseHelper.getCurrentUser() != null) {
            DatabaseReference allPostsReference = FirebaseDatabase.getInstance().getReference().child("PostCreating").child(postId);
            allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        handleOriginalPost(snapshot);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase RealmTime Database error", "Sign in up for the activity " + error.getMessage());
                }
            });
        }
    }

    private void handleOriginalPost(DataSnapshot originalPostSnapshot) {
        DatabaseReference savedPostsReference = FirebaseDatabase.getInstance().getReference().child("SavedPostCreating").child(currentUserId).child(postId);
        savedPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    PostCreating originalPost = originalPostSnapshot.getValue(PostCreating.class);
                    if (originalPost != null) {
                        if (!originalPost.getUserId().equals(currentUserId)) {
                            handleSaveForOtherUser(originalPost);
                        } else {
                            Toast.makeText(view.getContext(), "To Twój post!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(view.getContext(), "Post został już przez Ciebie zapisany", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase RealmTime Database error", "Sign in up for the activity/saving post " + error.getMessage());
            }
        });
    }

    private void handleSaveForOtherUser(PostCreating originalPost) {
        PostCreatingCopy newSavedPost = createSavedPostCopy(originalPost);
        DatabaseReference allPostsReference = FirebaseDatabase.getInstance().getReference().child("PostCreating").child(postId);

        if (originalPost.getPeopleSignedUp() < originalPost.getHowManyPeopleNeeded()) {
            originalPost.userSignedUp(currentUserId);

            allPostsReference.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    PostCreating postCreating = currentData.getValue(PostCreating.class);

                    if (postCreating != null) {
                        if (postCreating.getPeopleSignedUp() < postCreating.getHowManyPeopleNeeded()) {
                            // User signs up for the post
                            postCreating.userSignedUp(currentUserId);
                            currentData.setValue(postCreating);

                            if (postCreating.getPeopleSignedUp() >= postCreating.getHowManyPeopleNeeded()) {
                                // Check if the post is full
                                Toast.makeText(view.getContext(), "Wygląda na to, że nie ma już miejsca", Toast.LENGTH_SHORT).show();
                                postCreating.setActivityFull(true);
                            }
                        } else {
                            Toast.makeText(view.getContext(), "Wygląda na to, że nie ma już miejsca", Toast.LENGTH_SHORT).show();
                            postCreating.setActivityFull(true);
                        }
                    }
                    currentData.setValue(postCreating);  // Update the data after processing

                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    if (error != null) {
                        Log.e("Firebase Update Error", "Saving chosen post in DB - SavedPostCreating " + error.getMessage());
                    } else if (committed) {
                        DatabaseReference savedPostReference = FirebaseDatabase.getInstance().getReference().child("SavedPostCreating").child(currentUserId).child(postId);
                        saveNewPostToDatabase(newSavedPost, savedPostReference);
                    }
                }
            });
        } else {
            Toast.makeText(view.getContext(), "Wygląda na to, że nie ma już miejsca", Toast.LENGTH_SHORT).show();
        }
    }

    private PostCreatingCopy createSavedPostCopy(PostCreating originalPost) {
        PostCreatingCopy newSavedPost = new PostCreatingCopy();
        newSavedPost.setUserIdCreator(originalPost.getUserId());
        newSavedPost.setUserIdSavedBy(currentUserId);
        newSavedPost.setPostId(originalPost.getPostId());
        newSavedPost.setSportType(originalPost.getSportType());
        newSavedPost.setCityName(originalPost.getCityName());
        newSavedPost.setDateTime(originalPost.getDateTime());
        newSavedPost.setHourTime(originalPost.getHourTime());
        newSavedPost.setSkillLevel(originalPost.getSkillLevel());
        newSavedPost.setAdditionalInfo(originalPost.getAdditionalInfo());
        newSavedPost.setSavedByUser(true);
        return newSavedPost;
    }

    private void saveNewPostToDatabase(PostCreatingCopy newSavedPost, DatabaseReference savedPostsReference) {
        savedPostsReference.setValue(newSavedPost, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                Intent intent = new Intent(view.getContext(), MainMenuPosts.class);
                view.getContext().startActivity(intent);
                Toast.makeText(view.getContext(), "Zapisano!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Firebase Save Error", "Saving post error just before refreshing main menu " + databaseError.getMessage());
            }
        });
    }
}

