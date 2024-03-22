package com.noisevisionproductions.playmeet.loginRegister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.ActivityMainMenu;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.util.Objects;

public class GoogleSignInHelper {
    private final Fragment fragment;
    @Nullable
    private String idToken;

    public GoogleSignInHelper(Fragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    public ActivityResultLauncher<Intent> getActivityResultLauncher() {
        return fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    idToken = account.getIdToken();
                    saveIdTokenInCache();
                    firebaseAuthGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.e("Google login error", "Google login error " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void firebaseAuthGoogle(String idToken) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            saveUserIdInDatabase(userId);

                            ToastManager.showToast(fragment.requireActivity(), fragment.getString(R.string.loginSuccessful));
                            Intent intent = new Intent(fragment.requireActivity(), ActivityMainMenu.class);
                            intent.putExtra("loggedIn", true);
                            fragment.startActivity(intent);
                        }
                    } else {
                        Snackbar.make(fragment.requireView(), fragment.getString(R.string.errorWhileLoggingIn) + Objects.requireNonNull(task.getException()).getMessage(), Snackbar.LENGTH_LONG)
                                .setTextColor(Color.RED).show();
                    }
                });
    }

    private void saveUserIdInDatabase(@NonNull String userId) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel");
        userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    UserModel userModel = new UserModel();
                    userModel.setUserId(userId);
                    userReference.child(userModel.getUserId()).setValue(userModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (fragment.getContext() != null) {
                    ToastManager.showToast(fragment.getContext(), fragment.getString(R.string.errorWhileLoggingIn) + error.getMessage());
                    Log.e("Firebase save userId", "Saving userID to database " + error.getMessage());
                }
            }
        });
    }

    private void saveIdTokenInCache() {
        if (fragment.getActivity() != null) {
            SharedPreferences sharedPreferences = fragment.getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("idToken", idToken);
            myEdit.apply();
        }
    }
}
