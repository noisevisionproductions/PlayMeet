package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.postsManagement.FirestorePostRepository;
import com.noisevisionproductions.playmeet.userManagement.OnCompletionListener;
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

    public void handleOriginalPost() {
        FirestorePostRepository firestorePostRepository = new FirestorePostRepository();
        database.collection("PostCreating").document(postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            PostModel postModel = documentSnapshot.toObject(PostModel.class);
                            if (postModel != null && postModel.getUserId().equals(currentUserId)) {
                                ToastManager.showToast(view.getContext(), "To Twój post!");
                            } else {
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
                                                    if (postModel != null && postModel.getPeopleSignedUp() >= postModel.getHowManyPeopleNeeded()) {
                                                        ToastManager.showToast(view.getContext(), "Ta aktywność jest już pełna.");
                                                        return;
                                                    }
                                                    firestorePostRepository.registerUserToPost(postId, currentUserId, new OnCompletionListener() {
                                                        @Override
                                                        public void onSuccess() {
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
                        } else {
                            ToastManager.showToast(view.getContext(), "Wybrany post nie istnieje");
                        }
                    } else {
                        ToastManager.showToast(view.getContext(), "Nie udało się pobrać informacji o poście: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }
}

