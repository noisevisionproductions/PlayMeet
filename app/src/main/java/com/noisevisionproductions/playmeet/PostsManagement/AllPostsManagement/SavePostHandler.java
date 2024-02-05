package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.Adapters.ToastManager;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;

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
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUserId);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                UserModel currentUser = userSnapshot.getValue(UserModel.class);
                if (currentUser != null && currentUser.getJoinedPostsCount() < 3) {
                    DatabaseReference savedPostsReference = FirebaseDatabase.getInstance().getReference().child("SavedPostCreating").child(currentUserId).child(postId);
                    savedPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                PostCreating originalPost = originalPostSnapshot.getValue(PostCreating.class);
                                if (originalPost != null) {
                                    if (!originalPost.getSignedUpUserIds().contains(currentUserId)) {
                                        if (!originalPost.getUserId().equals(currentUserId)) {
                                            handleSaveForOtherUser(originalPost);
                                        } else {
                                            ToastManager.showToast(view.getContext(), "To Twój post!");
                                        }
                                    } else {
                                        ToastManager.showToast(view.getContext(), "Jesteś już zapisany w tej aktywności");
                                    }
                                }
                            } else {
                                ToastManager.showToast(view.getContext(), "Post został już przez Ciebie zapisany");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase RealmTime Database error", "Sign in up for the activity/saving post " + error.getMessage());
                        }
                    });
                } else {
                    ToastManager.showToast(view.getContext(), "Nie możesz dołączyć do więcej niż 3 aktywności jednocześnie.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                                getNoSlotsInfo(view);
                                postCreating.setActivityFull(true);
                            }
                        } else {
                            getNoSlotsInfo(view);
                            postCreating.setActivityFull(true);
                        }
                    }
                    currentData.setValue(postCreating);

                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    if (error != null) {
                        Log.e("Firebase Update Error", "Saving chosen post in DB - SavedPostCreating " + error.getMessage());
                    } else if (committed) {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUserId);
                        userRef.runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                UserModel user = mutableData.getValue(UserModel.class);
                                if (user == null) {
                                    return Transaction.success(mutableData);
                                }

                                // Inkrementacja licznika dołączonych postów
                                user.setJoinedPostsCount(user.getJoinedPostsCount() + 1);
                                mutableData.setValue(user); // Zapisz zmianę w bazie danych

                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                if (error != null) {
                                    Log.e("Firebase User Update Error", "Updating joined posts count " + error.getMessage());
                                }
                            }
                        });
                        DatabaseReference savedPostReference = FirebaseDatabase.getInstance().getReference().child("SavedPostCreating").child(currentUserId).child(postId);
                        saveNewPostToDatabase(newSavedPost, savedPostReference);
                    }
                }
            });
        } else {
            getNoSlotsInfo(view);
        }
    }

    private void getNoSlotsInfo(View view) {
        ToastManager.showToast(view.getContext(), "Wygląda na to, że nie ma już miejsca");
    }

    private PostCreatingCopy createSavedPostCopy(PostCreating originalPost) {
        PostCreatingCopy newSavedPost = new PostCreatingCopy();
        newSavedPost.setUserId(originalPost.getUserId());
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
                ToastManager.showToast(view.getContext(), "Dołączono!");
                Intent intent = new Intent(view.getContext(), MainMenuPosts.class);
                view.getContext().startActivity(intent);
            } else {
                Log.e("Firebase Save Error", "Saving post error just before refreshing main menu " + databaseError.getMessage());
            }
        });
    }
}

