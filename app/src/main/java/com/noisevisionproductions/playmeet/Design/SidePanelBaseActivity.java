package com.noisevisionproductions.playmeet.Design;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.LoginRegister.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.LoginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserAccountLogic;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class SidePanelBaseActivity extends AppCompatActivity {
    private FirebaseAuthManager authenticationManager;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected NavigationView navigationView;
    protected View headerView;
    protected CircleImageView userAvatar;

    protected void setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        // ułatwienie otwierania i zamykania poprzez stworzenie ikony do tego na górnym pasku akcji
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // bierze pod uwagę stan DrawerLayout czy otwarty czy zamknięty, a następnie synchronizuje ten stan z ikoną na pasku akcji      drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        authenticationManager = new FirebaseAuthManager();

        // ustawia, że ikonka na pasku akcji służy właśnie do kontrolowania stanu DrawerLayout Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationViewSidePanel);

        headerView = navigationView.getHeaderView(0);
        userAvatar = headerView.findViewById(R.id.userAvatar);

        setUserAvatar();
    }

    private void setUserAvatar() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            String userId = firebaseHelper.getCurrentUser().getUid();
            firebaseHelper.getUserAvatar(this, userId, userAvatar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        // gdy użytkownik wraca do aktywności, to wywołuje metodę, która ustawia tekst ostatniej pozycji w pasku zadań zależnie od tego, czy użytkownik jest zalogowany czy nie
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
