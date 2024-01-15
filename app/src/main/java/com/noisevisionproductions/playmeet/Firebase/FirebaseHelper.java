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

    // po wywołaniu tej metody, pobieram aktualnie zalogowanego użytkownika
    public FirebaseUser getCurrentUser() {
        return firebaseUser;
    }

    // pobieram dane z podanej referencji, (jesli jest ona powiązana z aktualnie zalogowanym użytkownikiem) z bazy danych
    public void getData(ValueEventListener listener, String reference) {
        if (firebaseUser != null) {
            DatabaseReference userReference = databaseReference.child(reference).child(firebaseUser.getUid());
            userReference.addListenerForSingleValueEvent(listener);
        }
    }

    // aktualizuje bazę danych podając dane oraz referencję do niej, w której mają być te dane zapisane
    public void updateDataUsingHashMap(HashMap<String, Object> userModel, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure, String reference) {
        if (firebaseUser != null) {
            DatabaseReference userReference = databaseReference.child(reference).child(firebaseUser.getUid());
            userReference.updateChildren(userModel)
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener(onFailure);
        }
    }
}
