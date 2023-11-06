package com.example.zagrajmy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


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
        setContentView(R.layout.login_register_bottom_buttons);

        authManager = new AuthenticationManager();

        mAuth = FirebaseAuth.getInstance();
   /*     edytujPoleEmail = findViewById(R.id.email);
        edytujPoleHaslo = findViewById(R.id.password);
        Button przyciskLoginu = findViewById(R.id.loginButton);*/

        switchToLogin();
        switchToRegister();
/*
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
        });*/
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

    public void switchToLogin() {
        Button buttonLogin = findViewById(R.id.kliknijabyzalogowac);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerLogin, LoginFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("login")
                        .commit();
            }
        });
    }

    public void switchToRegister() {
        Button buttonRegister = findViewById(R.id.kliknijabyzarejestrowac);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerLogin, RegisterFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("login")
                        .commit();
            }
        });
    }
}