package com.example.zagrajmy.Design;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zagrajmy.LoginRegister.LoginAndRegisterActivity;
import com.example.zagrajmy.R;

import io.realm.Realm;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);
        setContentView(R.layout.activity_welcome_screen);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginAndRegisterActivity.class);

            startActivity(intent);
            finish();

        }, 500);
    }
}
