package com.example.zagrajmy;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

public class Register extends AppCompatActivity {

    TextInputEditText edytujPoleEmail, edytujPoleHaslo;
    Button przyciskRejestracji;
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
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        edytujPoleEmail = findViewById(R.id.email);
        edytujPoleHaslo = findViewById(R.id.hasloPierwsze);
        przyciskRejestracji = findViewById(R.id.registerButton);
      //  pasekPostepu = findViewById(R.id.pasekPostepu);
        textView = findViewById(R.id.zalogujSie);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        przyciskRejestracji.setOnClickListener(new View.OnClickListener() {
            final TextView hasloJeden = findViewById(R.id.hasloPierwsze);
            final TextView hasloDwa = findViewById(R.id.hasloDrugie);
           /*  final String password1 = hasloJeden.getText().toString();
             final String password2 = hasloDwa.getText().toString();*/


            @Override
            public void onClick(View view) {
                String email, password1, password2;
                email = String.valueOf(edytujPoleEmail.getText());
                password1 = String.valueOf(hasloJeden.getText());
                password2 = String.valueOf(hasloDwa.getText());


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Wprowadź email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {
                    Toast.makeText(Register.this, "Wprowadź hasło", Toast.LENGTH_SHORT).show();
                    return;
                }
             /*   if (password1.length() < 8 || password1.length() > 32) {
                    Toast.makeText(Register.this, "Niepoprawna długość hasła (min. 8 znaków, max. 32 znaki)", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if (!password2.equals(password1)) {
                    Toast.makeText(Register.this, "Hasła nie pasują do siebie.", Toast.LENGTH_SHORT).show();
                    return;
                }
                createAccountWithEmailAndPassword(email,password1);
            }
        });
    }

    public void createAccountWithEmailAndPassword(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Konto założone",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Błąd", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}