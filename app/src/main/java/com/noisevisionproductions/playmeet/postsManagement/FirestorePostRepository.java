package com.noisevisionproductions.playmeet.postsManagement;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.RegistrationModel;
import com.noisevisionproductions.playmeet.userManagement.OnCompletionListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class FirestorePostRepository implements PostRepository {
    private final FirebaseFirestore postReference = FirebaseFirestore.getInstance();

    @Override
    public void addPost(PostModel postModel, OnPostCreatedListener listener) {
        postReference.collection("PostCreating").add(postModel)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }

    public void updatePost(String postId, PostModel postModel, OnCompletionListener listener) {
        // bez tej metody, podczas tworzenia postu, postId sie nie zapisuje w bazie danych
        // przez asynchroniczną naturę firestore
        postReference.collection("PostCreating").document(postId).set(postModel)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void deleteUserPost(String postId, PostModel postModel, OnCompletionListener listener) {
        postReference.collection("PostCreating").document(postId).delete()
                .addOnSuccessListener(aVoid -> updateSignedUpCount(postId, false, listener))
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void registerUserToPost(String postId, String userId, OnCompletionListener listener) {
        RegistrationModel registrationModel = new RegistrationModel();
        registrationModel.setPostId(postId);
        registrationModel.setUserId(userId);
        registrationModel.setRegistrationDate(new Timestamp(new Date()));
        postReference.collection("registrations").add(registrationModel)
                .addOnSuccessListener(documentReference -> updateSignedUpCount(postId, true, listener))
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void removeUserFromRegistration(String postId, String userId, OnCompletionListener listener) {
        Query registrationQuery = postReference.collection("registrations")
                .whereEqualTo("postId", postId)
                .whereEqualTo("userId", userId);

        registrationQuery.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            String documentId = documentSnapshot.getId();
                            postReference.collection("registrations")
                                    .document(documentId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> updateSignedUpCount(postId, false, listener))
                                    .addOnFailureListener(e -> {
                                        Log.e("FirestoreError", "Operation failed" + e);
                                        listener.onFailure(e);
                                    });
                        }
                    } else {
                        Exception e = task.getException();
                        Log.e("FirestoreError", "Operation failed", e);
                        listener.onFailure(e);
                    }
                });
    }

    private void updateSignedUpCount(String postId, boolean increment, OnCompletionListener listener) {
        DocumentReference postDocReference = postReference.collection("PostCreating").document(postId);

        postReference.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot postSnapshot = transaction.get(postDocReference);
                    int currentSignedUpCount = Objects.requireNonNull(postSnapshot.getLong("signedUpCount")).intValue();
                    int howManyPeopleNeeded = Objects.requireNonNull(postSnapshot.getLong("howManyPeopleNeeded")).intValue();

                    int newSignedUpCount = increment ? currentSignedUpCount + 1 : currentSignedUpCount - 1;
                    boolean isActivityNowFull = newSignedUpCount >= howManyPeopleNeeded;
                    String newPeopleStatus = newSignedUpCount + "/" + howManyPeopleNeeded;

                    transaction.update(postDocReference, "signedUpCount", newSignedUpCount);
                    transaction.update(postDocReference, "isActivityFull", isActivityNowFull);
                    transaction.update(postDocReference, "peopleStatus", newPeopleStatus);
                    return null;
                }).addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void deleteAllUserPosts(String userId, OnCompletionListener listener, Runnable onComplete) {
        postReference.collection("PostCreating")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot postSnapshot : task.getResult()) {
                            postReference.collection("PostCreating")
                                    .document(postSnapshot.getId())
                                    .delete()
                                    .addOnFailureListener(listener::onFailure);
                        }
                        listener.onSuccess();
                        onComplete.run();
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    @Override
    public void deleteAllUserRegistrationsAndUpdatePosts(String userId, OnCompletionListener listener) {
        CollectionReference registrationReference = postReference.collection("registrations");
        registrationReference.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        listener.onFailure(task.getException());
                        return;
                    }

                    List<String> postIdsToUpdate = new ArrayList<>();
                    for (DocumentSnapshot registerSnapshot : task.getResult()) {
                        String postId = registerSnapshot.getString("postId");
                        if (postId != null && !postIdsToUpdate.contains(postId)) {
                            postIdsToUpdate.add(postId);
                        }
                        registrationReference.document(registerSnapshot.getId())
                                .delete()
                                .addOnFailureListener(listener::onFailure);
                    }
                    for (String postId : postIdsToUpdate) {
                        updateSignedUpCount(postId, false, new OnCompletionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("Firestore registration update", "Firestore registration update successful.");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("Firestore registration update", "Firestore registration update error " + e.getMessage());
                            }
                        });
                    }
                    listener.onSuccess();
                });
    }
}
