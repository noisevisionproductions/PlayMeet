package com.example.zagrajmy;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountManagementActivity extends AppCompatActivity {
    NavigationView navigationView;
    SidePanelMenu sidePanelMenu;
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigationViewSidePanel);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        logout();
        // logoutAccount();

    }

    public FirebaseUser getCurrentUser() {
        return user;
    }

    public AccountManagementActivity() {

    }

    public AccountManagementActivity(FirebaseUser user) {
        this.user = auth.getCurrentUser();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (user != null) {
            FirebaseAuth.getInstance().signOut();

        }
    }

    public void logout() {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.wylogujKontoPrzycisk) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Wylogowano", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;


            }
        });

    }

    public void logoutAccount() {
        button = findViewById(R.id.wylogujKontoPrzycisk);
        textView = findViewById(R.id.informacjeOUzytkowniku);
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "Wylogowano", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });
    }

}
