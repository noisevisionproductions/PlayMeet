package com.example.zagrajmy.LoginRegister;

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
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.example.zagrajmy.Realm.RealmDataManager;
import com.example.zagrajmy.PostsManagement.MainMenuPosts;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.Realm.RealmAuthenticationManager;
import com.example.zagrajmy.UserManagement.UserModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class LoginFragment extends Fragment {
    private RealmAuthenticationManager authManager;
    private String email, password;
    private TextInputEditText edytujPoleEmail, edytujPoleHaslo;
    private final RealmDataManager realmDataManager = RealmDataManager.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.login_fragment, container, false);

        authManager = new RealmAuthenticationManager();

        if (authManager.isUserLoggedIn()) {
            Intent intent = new Intent(getContext(), MainMenuPosts.class);
            startActivity(intent);
        }

        guestButton(currentView);

        edytujPoleEmail = currentView.findViewById(R.id.email);
        edytujPoleHaslo = currentView.findViewById(R.id.password);

        loginUserLogic(currentView);

        return currentView;
    }

    public void loginUserLogic(View view) {
        AppCompatButton loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            email = String.valueOf(edytujPoleEmail.getText());
            password = String.valueOf(edytujPoleHaslo.getText());

            if (emptyLoginFieldsErrorHandle()) {
                return;
            }

            authManager.userLogin(email, password, task -> {
                if (task.isSuccess()) {
                    App app = RealmAppConfig.getApp();
                    User currentUser = app.currentUser();

                    if (currentUser != null) {
                        String userId = currentUser.getId();
                        try (Realm realm = Realm.getDefaultInstance()) {
                            UserModel userModel = realm.where(UserModel.class)
                                    .equalTo("userId", userId)
                                    .findFirst();

                            if (userModel == null) {
                                userModel = new UserModel();
                                userModel.setUserId(userId);
                                realmDataManager.addUser(userModel);
                            }
                        }

                        Toast.makeText(getActivity(), "Pomyślnie zalogowano", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainMenuPosts.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "User is null",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = Objects.requireNonNull(task.getError()).getMessage();
                    Toast.makeText(getActivity(), "Authentication failed: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                    assert errorMessage != null;
                    Log.d(TAG, errorMessage);
                }
            });
        });
    }

    public boolean emptyLoginFieldsErrorHandle() {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Wprowadź e-mail oraz haslo", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Wprowadź hasło", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Wprowadź e-mail", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void guestButton(View view) {
        AppCompatTextView guestButton = view.findViewById(R.id.continueAsGuest);
        guestButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainMenuPosts.class);
            startActivity(intent);
        });
    }
}
