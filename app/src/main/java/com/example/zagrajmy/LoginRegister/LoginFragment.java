package com.example.zagrajmy.LoginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.MainMenu;
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private AuthenticationManager authManager;
    private String email, password;
    private TextInputEditText edytujPoleEmail, edytujPoleHaslo;
    private final RealmDatabaseManagement realmDatabaseManagement = RealmDatabaseManagement.getInstance();

    @Override
    public void onDestroy() {
        realmDatabaseManagement.closeRealmDatabase();
        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View currentView = inflater.inflate(R.layout.login_fragment, container, false);


        authManager = new AuthenticationManager();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(getContext(), MainMenu.class);
            startActivity(intent);
        }

        edytujPoleEmail = currentView.findViewById(R.id.email);
        edytujPoleHaslo = currentView.findViewById(R.id.password);

        Button loginButton = currentView.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(view -> {
            email = String.valueOf(edytujPoleEmail.getText());
            password = String.valueOf(edytujPoleHaslo.getText());

            if (emptyLoginFieldsErrorHandle()) {
                return;
            }

            authManager.userLogin(email, password, task -> {
                if (task.isSuccessful()) {
                    User userClass = new User();
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser().getUid());
                    userClass.setUserId(userId);
                    realmDatabaseManagement.addUser(userClass);

                    //   UserUidManager.getInstance().setUser(userClass);

                    Toast.makeText(getActivity(), "Zalogowano", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MainMenu.class);
                    startActivity(intent);


                } else {
                    String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(getActivity(), "Authentication failed: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
        return currentView;
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
}
