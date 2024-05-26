package com.noisevisionproductions.playmeet.firebase;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnPostsFetchedListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.PostDisplay;

import java.util.ArrayList;
import java.util.List;

public class FirestorePostsDisplay implements PostDisplay {
    private final FirebaseFirestore postReference = FirebaseFirestore.getInstance();
    private Query query;

    public FirestorePostsDisplay() {
    }

    public FirestorePostsDisplay(boolean isUserLoggedIn, String userId, String city) {
        if (isUserLoggedIn) {
            query = postReference.collection("PostCreating")
                    .whereNotEqualTo("userId", userId)
                    .whereEqualTo("isActivityFull", false)
                    .whereEqualTo("cityName", city);
        } else {
            query = postReference.collection("PostCreating")
                    .whereEqualTo("isActivityFull", false)
                    .whereEqualTo("cityName", city);
        }
        setQuery(query);
    }

    public FirestorePostsDisplay(boolean isUserLoggedIn, String userId) {
        if (isUserLoggedIn) {
            query = postReference.collection("PostCreating")
                    .whereNotEqualTo("userId", userId)
                    .whereEqualTo("isActivityFull", false);
        } else {
            query = postReference.collection("PostCreating")
                    .whereEqualTo("isActivityFull", false);
        }
        setQuery(query);
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Query getQuery() {
        return this.query;
    }

    @Override
    public void getUserPosts(String userId, OnPostsFetchedListener listener) {
        Query userPosts = postReference.collection("PostCreating")
                .whereEqualTo("userId", userId);
        userPosts.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<PostModel> posts = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    posts.add(documentSnapshot.toObject(PostModel.class));
                }
                listener.onPostsFetched(posts, null);
            } else {
                listener.onPostsFetched(null, task.getException());
            }
        });
    }

    @Override
    public void getRegisteredPosts(String userId, OnPostsFetchedListener listener) {
        postReference.collection("registrations")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> registeredPostIds = new ArrayList<>();
                        for (QueryDocumentSnapshot registrationSnapshot : task.getResult()) {
                            registeredPostIds.add(registrationSnapshot.getString("postId"));
                        }
                        getPostsDetails(registeredPostIds, listener);
                    } else {
                        listener.onPostsFetched(null, task.getException());
                    }
                });
    }

    private void getPostsDetails(List<String> postIds, OnPostsFetchedListener listener) {
        List<PostModel> registeredPosts = new ArrayList<>();
        if (postIds.isEmpty()) {
            listener.onPostsFetched(registeredPosts, null);
            return;
        }
        postReference.collection("PostCreating")
                // whereIn ma ograniczenie do 10 postow jednoczesnie!!
                .whereIn(FieldPath.documentId(), postIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot postSnapshot : task.getResult()) {
                            registeredPosts.add(postSnapshot.toObject(PostModel.class));
                        }
                        listener.onPostsFetched(registeredPosts, null);
                    } else {
                        listener.onPostsFetched(null, task.getException());
                    }
                });
    }
}
