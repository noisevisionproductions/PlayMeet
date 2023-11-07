package com.example.zagrajmy;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import io.realm.Realm;


public class LoginAndRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);

        setContentView(R.layout.login_register_main_buttons);

        switchToLogin();
        switchToRegister();
    }

    public void switchToLogin() {
        Button buttonLogin = findViewById(R.id.kliknijabyzalogowac);

        buttonLogin.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerLogin, LoginFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("login")
                    .commit();
        });
    }

    public void switchToRegister() {
        Button buttonRegister = findViewById(R.id.kliknijabyzarejestrowac);

        buttonRegister.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerLogin, RegisterFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("login")
                    .commit();
        });
    }
}