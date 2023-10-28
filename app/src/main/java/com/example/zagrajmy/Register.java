package com.example.zagrajmy;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Register extends AppCompatActivity {

    private String email, passwordFirst, passwordSecond;
    private TextInputEditText edytujPoleEmail;
    private FirebaseAuth mAuth;

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
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        edytujPoleEmail = findViewById(R.id.email);
        Button przyciskRejestracji = findViewById(R.id.registerButton);
        TextView textView = findViewById(R.id.zalogujSie);
        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        przyciskRejestracji.setOnClickListener(new View.OnClickListener() {
            final TextView hasloJeden = findViewById(R.id.hasloPierwsze);
            final TextView hasloDwa = findViewById(R.id.hasloDrugie);


            @Override
            public void onClick(View view) {
                email = String.valueOf(edytujPoleEmail.getText());
                passwordFirst = String.valueOf(hasloJeden.getText());
                passwordSecond = String.valueOf(hasloDwa.getText());

                if (emptyFieldsErrorHandle()){
                    return;
                }

                createAccountWithEmailAndPassword(email,passwordFirst);
            }
        });
    }

    public void createAccountWithEmailAndPassword(String email, String passwordFirst) {
        AuthenticationManager authManager = new AuthenticationManager();

        authManager.userRegister(email, passwordFirst, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Register.this, "Konto założone",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AccountManagementActivity.class);
                startActivity(intent);
                finish();
            } else {
                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(Register.this, "Authentication failed: " + errorMessage,
                        Toast.LENGTH_SHORT).show();            }
        });
    }

    public boolean emptyFieldsErrorHandle(){
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Register.this, "Wprowadź e-mail", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(passwordFirst) || TextUtils.isEmpty(passwordSecond)) {
            Toast.makeText(Register.this, "Wprowadź hasło", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!passwordSecond.equals(passwordFirst)) {
            Toast.makeText(Register.this, "Hasła nie pasują do siebie.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}