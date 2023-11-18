package com.example.zagrajmy.UserManagement;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserProfileManager {

    public UserProfileManager(FirebaseUser user){
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
    }
    public void updateUserProfile(String nickname){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(taskk -> {
                    if (taskk.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                    } else {
                        Log.w(TAG, "User profile update failed.", taskk.getException());
                    }
                });
    }
}
