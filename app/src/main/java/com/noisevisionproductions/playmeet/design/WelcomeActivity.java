package com.noisevisionproductions.playmeet.design;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.loginRegister.LoginAndRegisterActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inicjalizuje Firebase zaraz na starcie aplikacji
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_welcome_screen);

        ImageView img = findViewById(R.id.welcomeImage);
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.spinning_ball);
        img.startAnimation(rotateAnimation);

        // gdy aplikacja się uruchamia, to ustawiam czas jak długo ma trwać ekran powitalny oraz wyświetlam layout powitalny
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginAndRegisterActivity.class);

            startActivity(intent);
            finish();

        }, 1000);
    }
}