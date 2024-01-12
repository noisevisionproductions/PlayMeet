package com.noisevisionproductions.playmeet.Design;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.noisevisionproductions.playmeet.LoginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.LoginRegister.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserAccountLogic;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public abstract class SidePanelBaseActivity extends AppCompatActivity {
    private FirebaseAuthManager authenticationManager;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected NavigationView navigationView;

    protected void setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        authenticationManager = new FirebaseAuthManager();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationViewSidePanel);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        updateLoginMenuItemTitle();
    }

    private void updateLoginMenuItemTitle() {
        MenuItem loginItem = navigationView.getMenu().findItem(R.id.wylogujKonto);
        if (authenticationManager.isUserLoggedIn()) {
            loginItem.setTitle("Wyloguj konto");
        } else {
            loginItem.setTitle("Zaloguj się");
        }
    }

    protected void setupNavigationView() {
        this.navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.userProfile) {
                // Toast.makeText(getApplicationContext(), "Dostępne jedynie dla zalogowanych użytkowników!", Toast.LENGTH_SHORT).show();
                if (!this.getClass().getName().equals(UserAccountLogic.class.getName())) {
                    Intent intent = new Intent(getApplicationContext(), UserAccountLogic.class);
                    startActivity(intent);
                }
            }

            if (id == R.id.mojaAktywnosc) {
                if (!this.getClass().getName().equals(MainMenuPosts.class.getName())) {
                    Intent intent = new Intent(getApplicationContext(), MainMenuPosts.class);
                    startActivity(intent);
                }
            }

            if (authenticationManager.isUserLoggedIn()) {
                if (id == R.id.wylogujKonto) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        firebaseAuth.signOut();
                        Toast.makeText(getApplicationContext(), "Pomyślnie wylogowano", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                        startActivity(intent);
                    }
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

}
