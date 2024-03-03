package com.noisevisionproductions.playmeet.firebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseAuthManager {
    @NonNull
    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthManager() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void userLogin(@NonNull String email, @NonNull String password, @NonNull OnCompleteListener<AuthResult> callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(callback);
    }

    public void userRegister(@NonNull String email, @NonNull String password, @NonNull OnCompleteListener<AuthResult> callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(callback);
    }

    public static boolean isUserLoggedIn() {
        FirebaseAuth firebase = FirebaseAuth.getInstance();
        // sprawdzam, czy użytkownik jest ogólnie zalogowany
        return firebase.getCurrentUser() != null;
    }

    public static boolean isUserLoggedInUsingGoogle() {
        FirebaseAuth firebase = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebase.getCurrentUser();
        if (firebaseUser != null) {
            String lastProviderId = firebaseUser.getProviderData().get(firebaseUser.getProviderData().size() - 1).getProviderId();
            return lastProviderId.equals(GoogleAuthProvider.PROVIDER_ID);
        }
        return false;
    }

}
