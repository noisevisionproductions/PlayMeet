package com.noisevisionproductions.playmeet.utilities.admin;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminManager {
    private final FirebaseFirestore adminDatabase;

    public AdminManager() {
        adminDatabase = FirebaseFirestore.getInstance();
    }

    public void checkAdmin(String userId, final AdminCheckCallback callback) {
        DocumentReference userReference = adminDatabase.collection("Admins").document(userId);
        userReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                boolean isAdmin = documentSnapshot.exists();
                callback.onAdminCheckComplete(isAdmin);
            } else {
                callback.onAdminCheckComplete(false);
            }
        });
    }

    public interface AdminCheckCallback {
        void onAdminCheckComplete(boolean isAdmin);
    }
}
