package com.noisevisionproductions.playmeet.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.noisevisionproductions.playmeet.ActivityMainMenu;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.sql.SQLException;

public class FirestoreUtil {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference postCollection = db.collection("PostCreating");

    public FirestoreUtil() {
    }

    public void insertPost(PostCreating postCreating, Context context) {
        postCollection.add(postCreating)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ToastManager.showToast(context, "Post utworzony!");
                        Intent intent = new Intent(context, ActivityMainMenu.class);
                        context.startActivity(intent);
                        Log.d("saving post to firestore", "Post added with ID: " + task.getResult().getId());
                    } else {
                        Log.e("saving post to firestore", "Error adding post " + task.getResult().getId());
                    }
                });
    }

    public static void updatePost(PostCreating postCreating) throws SQLException {

    }

    public static void deletePost(int postId) throws SQLException {

    }
}
