package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.bottomSheetFragment;

import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirebaseUserRepository;
import com.noisevisionproductions.playmeet.firebase.FirestorePostRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnJoinedPostsCountListener;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.util.Objects;

public class SavePostHandler {
    private final View view;
    private final String postId;
    private String currentUserId;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public SavePostHandler(View view, String postId) {
        this.view = view;
        this.postId = postId;
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            this.currentUserId = firebaseHelper.getCurrentUser().getUid();
        }
    }

    public void savePostInDBLogic() {
        FirestorePostRepository firestorePostRepository = new FirestorePostRepository();
        FirebaseUserRepository firebaseUserRepository = new FirebaseUserRepository();

        database.collection("PostCreating").document(postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            PostModel postModel = documentSnapshot.toObject(PostModel.class);
                            if (postModel != null) {
                                if (postModel.getUserId().equals(currentUserId)) {
                                    ToastManager.showToast(view.getContext(), "To Twój post!");
                                } else {
                                    handlePostSubscription(firebaseUserRepository, firestorePostRepository, postModel);
                                }
                            } else {
                                ToastManager.showToast(view.getContext(), "Wybrany post nie istnieje");
                            }
                        } else {
                            ToastManager.showToast(view.getContext(), "Nie udało się pobrać informacji o poście.");
                        }
                    } else {
                        ToastManager.showToast(view.getContext(), "Nie udało się pobrać informacji o poście: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private void handlePostSubscription(FirebaseUserRepository firebaseUserRepository, FirestorePostRepository firestorePostRepository, PostModel postModel) {
        // Sprawdź limit postów tylko jeśli nie jest to post użytkownika
        firebaseUserRepository.getJoinedPostsCount(currentUserId, new OnJoinedPostsCountListener() {
            @Override
            public void onCountReceived(int count) {
                if (count >= 3) {
                    ToastManager.showToast(view.getContext(), "Osiągnąłeś limit 3 zapisanych aktywności.");
                    return;
                }
                checkRegistrationAndSignUpForPost(firebaseUserRepository, firestorePostRepository, postModel);
            }

            @Override
            public void onFailure(Exception e) {
                ToastManager.showToast(view.getContext(), "Błąd podczas sprawdzania limitu postów: " + e.getMessage());
            }
        });
    }

    private void checkRegistrationAndSignUpForPost(FirebaseUserRepository firebaseUserRepository, FirestorePostRepository firestorePostRepository, PostModel postModel) {
        database.collection("registrations")
                .whereEqualTo("postId", postId)
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(registrationTask -> {
                    if (registrationTask.isSuccessful()) {
                        QuerySnapshot querySnapshot = registrationTask.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            ToastManager.showToast(view.getContext(), "Jesteś już zapisany do tej aktywności");
                        } else {
                            if (postModel != null && postModel.getSignedUpCount() >= postModel.getHowManyPeopleNeeded()) {
                                ToastManager.showToast(view.getContext(), "Ta aktywność jest już pełna.");
                                return;
                            }
                            firestorePostRepository.registerUserToPost(postId, currentUserId, new OnCompletionListener() {
                                @Override
                                public void onSuccess() {
                                    firebaseUserRepository.incrementJoinedPostsCount(currentUserId, new OnCompletionListener() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("User joined posts updated", "User joined posts updated");
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e("User joined posts error", "User joined posts updated" + e.getMessage());
                                        }
                                    });
                                    ToastManager.showToast(view.getContext(), "Zapisano!");
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    ToastManager.showToast(view.getContext(), "Błąd podczas zapisywania " + e.getMessage());
                                }
                            });
                        }
                    } else {
                        ToastManager.showToast(view.getContext(), "Błąd podczas zapisywania " + Objects.requireNonNull(registrationTask.getException()).getMessage());
                    }
                });
    }
}

