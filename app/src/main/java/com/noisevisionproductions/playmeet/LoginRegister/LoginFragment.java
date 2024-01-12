package com.noisevisionproductions.playmeet.LoginRegister;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private FirebaseAuthManager authManager;
    private String emailString, passwordString;
    private AppCompatAutoCompleteTextView emailInput, passwordInput;
    private AppCompatButton loginButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        getUIObjects(view);
        guestButton(view);

        if (authManager.isUserLoggedIn()) {
            Intent intent = new Intent(getContext(), MainMenuPosts.class);
            startActivity(intent);
        } else {
            loginButton.setOnClickListener(this::loginUser);
        }

        return view;
    }

    public void getUIObjects(View view) {
        authManager = new FirebaseAuthManager();

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
    }

    public void loginUser(View view) {
        emailString = String.valueOf(emailInput.getText());
        passwordString = String.valueOf(passwordInput.getText());

        if (!checkValidation()) {
            authManager.userLogin(emailString, passwordString, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid();

                        UserModel userModel = new UserModel();
                        userModel.setUserId(userId);
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("UserModel");
                        usersRef.child(userModel.getUserId()).setValue(userModel);

                        Toast.makeText(getActivity(), "Pomyślnie zalogowano", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainMenuPosts.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Użytkownik nie istnieje",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(getActivity(), "Błąd uwierzytelnienia: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                    if (errorMessage != null) {
                        Log.d(TAG, errorMessage);
                    }
                }
            });
        }
    }

    public boolean checkValidation() {
        boolean isError = false;

        if (TextUtils.isEmpty(emailString)) {
            emailInput.setError("Wprowadź e-mail");
            isError = true;
        }
        if (TextUtils.isEmpty(passwordString)) {
            passwordInput.setError("Wprowadź hasło");
            isError = true;
        }

        return isError;
    }

    public void guestButton(View view) {
        AppCompatTextView guestButton = view.findViewById(R.id.continueAsGuest);
        guestButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainMenuPosts.class);
            startActivity(intent);
        });
    }
}
