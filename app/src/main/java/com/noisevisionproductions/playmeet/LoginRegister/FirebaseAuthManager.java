package com.noisevisionproductions.playmeet.LoginRegister;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthManager {
    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthManager() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void userLogin(String email, String password, OnCompleteListener<AuthResult> callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(callback);
    }

    public void userRegister(String email, String password, OnCompleteListener<AuthResult> callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(callback);
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }
}
