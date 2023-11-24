package com.example.zagrajmy.LoginRegister;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.zagrajmy.R;


public class LoginAndRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       /* RealmDatabaseManagement realm = RealmDatabaseManagement.getInstance();
        realm.deleteAllRealmDataUseForTestingOnly();
        realm.realmMigrationResetDatabaseOnlyForTesting();
        realm.closeRealmDatabase();*/
        setContentView(R.layout.activity_login_register);

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
                    .commit();
        });
    }
}