package com.noisevisionproductions.playmeet.loginRegister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.loginRegister.onboarding.OnboardingActivity;

public class LoginAndRegisterActivity extends AppCompatActivity {
    private AppCompatButton buttonLogin, buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_register);

        buttonLogin = findViewById(R.id.kliknijabyzalogowac);
        buttonRegister = findViewById(R.id.kliknijabyzarejestrowac);

        buttonLogin.setSelected(true);

        onboardingSetup();
        switchToLogin();
        switchToRegister();
        backPressed();
    }

    private void onboardingSetup() {
        SharedPreferences preferences = getSharedPreferences("MySharedPref", 0);
        boolean firstStart = preferences.getBoolean("firstStart", true);

        if (firstStart) {
            startActivity(new Intent(this, OnboardingActivity.class));

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }
    }

    public void switchToLogin() {
        buttonLogin.setOnClickListener(view -> {

            buttonLogin.setSelected(true);
            buttonRegister.setSelected(false);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerLogin, LoginFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        });
    }

    public void switchToRegister() {
        buttonRegister.setOnClickListener(view -> {

            buttonRegister.setSelected(true);
            buttonLogin.setSelected(false);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerLogin, RegisterFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        });
    }

    private void backPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}