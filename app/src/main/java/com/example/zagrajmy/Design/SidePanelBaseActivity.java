package com.example.zagrajmy.Design;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.LoginRegister.LoginAndRegisterActivity;
import com.example.zagrajmy.PostsManagement.MainMenuPosts;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.Realm.RealmAuthenticationManager;
import com.example.zagrajmy.UserManagement.UserAccountLogic;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import io.realm.mongodb.App;

public abstract class SidePanelBaseActivity extends AppCompatActivity {
    private RealmAuthenticationManager authenticationManager;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected NavigationView navigationView;

    protected void setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        authenticationManager = new RealmAuthenticationManager();

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
                    App app = RealmAppConfig.getApp();
                    io.realm.mongodb.User currentUser = app.currentUser();
                    if (currentUser != null) {
                        currentUser.logOutAsync(result -> {
                            if (result.isSuccess()) {
                                Toast.makeText(getApplicationContext(), "Pomyślnie wylogowano", Toast.LENGTH_SHORT).show();
                                RealmDatabaseManagement realmDatabaseManagement = new RealmDatabaseManagement();
                                realmDatabaseManagement.closeRealmDatabase();
                                Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                                startActivity(intent);
                            } else {
                                Log.e("AUTH", result.getError().toString());
                            }
                        });
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
