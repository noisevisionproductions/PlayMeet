package com.example.zagrajmy.Design;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.LoginRegister.LoginAndRegisterActivity;
import com.example.zagrajmy.PostsManagement.UserPosts.UsersActivePosts;
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

    protected void setupNavigationView() {
        this.navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.wylogujKonto) {
                FirebaseAuth.getInstance().signOut();
                RealmDatabaseManagement realmDatabaseManagement = new RealmDatabaseManagement();
                realmDatabaseManagement.closeRealmDatabase();
                Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                startActivity(intent);
                finish();
            }
            if (id == R.id.userProfile) {
                Intent intent = new Intent(getApplicationContext(), UserAccountLogic.class);
                startActivity(intent);
                finish();
            }

            if (id == R.id.mojaAktywnosc) {
                Intent intent = new Intent(getApplicationContext(), UsersActivePosts.class);
                startActivity(intent);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

}
