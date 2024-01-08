package com.example.zagrajmy.LoginRegister;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import com.example.zagrajmy.R;


public class LoginAndRegisterActivity extends AppCompatActivity {
    private AppCompatButton buttonLogin, buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_register);

        buttonLogin = findViewById(R.id.kliknijabyzalogowac);
        buttonRegister = findViewById(R.id.kliknijabyzarejestrowac);

        buttonLogin.setSelected(true);

        switchToLogin();
        switchToRegister();
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
}