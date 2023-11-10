package com.example.zagrajmy.LoginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.zagrajmy.LoginRegister.AuthenticationManager;
import com.example.zagrajmy.MainMenu;
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private AuthenticationManager authManager;
    private String email, password;
    private TextInputEditText edytujPoleEmail, edytujPoleHaslo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View currentView = inflater.inflate(R.layout.activity_login, container, false);

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
