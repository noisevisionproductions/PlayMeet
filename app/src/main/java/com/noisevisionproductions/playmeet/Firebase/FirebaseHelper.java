package com.noisevisionproductions.playmeet.Firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FirebaseHelper {
    private final DatabaseReference databaseReference;
    private final FirebaseUser firebaseUser;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseUser;
    }

    public void getData(ValueEventListener listener, String reference) {
        if (firebaseUser != null) {
            DatabaseReference userReference = databaseReference.child(reference).child(firebaseUser.getUid());
            userReference.addListenerForSingleValueEvent(listener);
        }
    }

    public void updateDataUsingHashMap(HashMap<String, Object> userModel, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure, String reference) {
        if (firebaseUser != null) {
            DatabaseReference userReference = databaseReference.child(reference).child(firebaseUser.getUid());
            userReference.updateChildren(userModel)
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener(onFailure);
        }
    }
}
