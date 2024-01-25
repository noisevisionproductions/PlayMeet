package com.noisevisionproductions.playmeet.Firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

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

    public static boolean isUserLoggedIn() {
        FirebaseAuth firebase = FirebaseAuth.getInstance();
        // sprawdzam, czy użytkownik jest ogólnie zalogowany
        return firebase.getCurrentUser() != null;
    }

    public static boolean isUserLoggedInUsingGoogle() {
        FirebaseAuth firebase = FirebaseAuth.getInstance();
        // sprwadzam, czy logowanie było poprzez Google
        FirebaseUser firebaseUser = firebase.getCurrentUser();
        if (firebaseUser != null) {
            for (UserInfo userInfo : firebaseUser.getProviderData()) {
                if (userInfo.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)) {
                    return true;
                }
            }
        }
        return false;
    }
}
