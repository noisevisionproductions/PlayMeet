package com.example.zagrajmy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextInputEditText edytujPoleEmail, edytujPoleHaslo;
    Button przyciskLoginu;
    ProgressBar pasekPostepu;
    FirebaseAuth mAuth;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        edytujPoleEmail = findViewById(R.id.email);
        edytujPoleHaslo = findViewById(R.id.password);
        przyciskLoginu = findViewById(R.id.loginButton);
       // pasekPostepu = findViewById(R.id.pasekPostepu);
        textView = findViewById(R.id.stworzKonto);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        przyciskLoginu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(edytujPoleEmail.getText());
                password = String.valueOf(edytujPoleHaslo.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Wprowadź email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Wprowadź hasło", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*if (password.length() < 7){
                    Toast.makeText(Login.this, "Hasło jest zbyt krótkie (min. 8 znaków)", Toast.LENGTH_SHORT).show();
                    return;
                }  */
                if (password.length() > 32){
                    Toast.makeText(Login.this, "Hasło jest zbyt długie (max. 32 znaków)", Toast.LENGTH_SHORT).show();
                    return;
                }

                zalogujUzytkownika(email, password);
            }
        });
    }

    public void zalogujUzytkownika(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Zalogowano", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), AccountManagementActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}