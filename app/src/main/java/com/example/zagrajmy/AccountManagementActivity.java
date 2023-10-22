package com.example.zagrajmy;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountManagementActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        logoutAccount();

    }

    public FirebaseUser getCurrentUser() {
        return user;
    }
    public AccountManagementActivity() {

    }
    public AccountManagementActivity(FirebaseUser user){
        this.user = auth.getCurrentUser();

    }
    @Override
    protected void onStop() {
        super.onStop();

        if (user != null) {
            FirebaseAuth.getInstance().signOut();

        }
    }

    public void logoutAccount(){
        button = findViewById(R.id.wylogujKontoPrzycisk);
        textView = findViewById(R.id.informacjeOUzytkowniku);
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Wylogowano", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
