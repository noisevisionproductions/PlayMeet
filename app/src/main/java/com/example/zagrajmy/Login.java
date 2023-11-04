package com.example.zagrajmy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class Login extends AppCompatActivity {

    private AuthenticationManager authManager;
    private String email, password;
    private TextInputEditText edytujPoleEmail, edytujPoleHaslo;
    private FirebaseAuth mAuth;
    private TextView textView;


    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthenticationManager();

        mAuth = FirebaseAuth.getInstance();
        edytujPoleEmail = findViewById(R.id.email);
        edytujPoleHaslo = findViewById(R.id.password);
        Button przyciskLoginu = findViewById(R.id.loginButton);
        textView = findViewById(R.id.stworzKonto);

        switchToRegister();

        przyciskLoginu.setOnClickListener(view -> {
            email = String.valueOf(edytujPoleEmail.getText());
            password = String.valueOf(edytujPoleHaslo.getText());

            if (emptyLoginFieldsErrorHandle()) {
                return;
            }

            authManager.userLogin(email, password, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Zalogowano", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(Login.this, "Authentication failed: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public boolean emptyLoginFieldsErrorHandle() {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(Login.this, "Wprowadź e-mail oraz haslo", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Login.this, "Wprowadź hasło", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Login.this, "Wprowadź e-mail", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void switchToRegister() {
        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });
    }
}