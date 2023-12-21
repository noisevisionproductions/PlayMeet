package com.example.zagrajmy.Design;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.LoginRegister.AuthenticationManager;
import com.example.zagrajmy.LoginRegister.LoginAndRegisterActivity;
import com.example.zagrajmy.PostsManagement.MainMenuPosts;
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.UserAccountLogic;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public abstract class SidePanelBaseActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected NavigationView navigationView;

    protected void setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

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
        MenuItem loginItem = navigationView.getMenu().findItem(R.id.wylogujKonto);

        if (AuthenticationManager.isUserLoggedIn()) {
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

            if (AuthenticationManager.isUserLoggedIn()) {
                if (id == R.id.wylogujKonto) {
                    FirebaseAuth.getInstance().signOut();
                    RealmDatabaseManagement realmDatabaseManagement = new RealmDatabaseManagement();
                    realmDatabaseManagement.closeRealmDatabase();
                    Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                    startActivity(intent);
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
