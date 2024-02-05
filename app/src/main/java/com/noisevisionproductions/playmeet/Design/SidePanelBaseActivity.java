package com.noisevisionproductions.playmeet.Design;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noisevisionproductions.playmeet.Adapters.ToastManager;
import com.noisevisionproductions.playmeet.Design.AboutApp.AboutAppActivity;
import com.noisevisionproductions.playmeet.Firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.LoginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.AvatarManagement;
import com.noisevisionproductions.playmeet.UserManagement.UserAccountLogic;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class SidePanelBaseActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected NavigationView navigationView;
    protected View headerView;
    protected CircleImageView userAvatar;
    protected AppCompatButton addPhotoButton;

    protected void setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        // ułatwienie otwierania i zamykania poprzez stworzenie ikony do tego na górnym pasku akcji
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        // bierze pod uwagę stan DrawerLayout czy otwarty czy zamknięty, a następnie synchronizuje ten stan z ikoną na pasku akcji
        actionBarDrawerToggle.syncState();

        // ustawia, że ikonka na pasku akcji służy właśnie do kontrolowania stanu DrawerLayout
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationViewSidePanel);

        addPhotoButton = navigationView.getHeaderView(0).findViewById(R.id.addPhotoButton);
        headerView = navigationView.getHeaderView(0);
        userAvatar = headerView.findViewById(R.id.userAvatar);

        getUserAvatar();
        setUserAvatar();
    }

    private void getUserAvatar() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            String userId = firebaseHelper.getCurrentUser().getUid();
            firebaseHelper.getUserAvatar(this, userId, userAvatar);
        }
    }

    private void setUserAvatar() {
        if (FirebaseAuthManager.isUserLoggedIn()) {
            AvatarManagement avatarManagement = new AvatarManagement(this, addPhotoButton);
            avatarManagement.setupListeners();
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
        MenuItem loginItem = navigationView.getMenu().findItem(R.id.logoutAccount);
        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
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

            if (id == R.id.myActivity) {
                if (!this.getClass().getName().equals(MainMenuPosts.class.getName())) {
                    Intent intent = new Intent(getApplicationContext(), MainMenuPosts.class);
                    startActivity(intent);
                }
            }

            if (id == R.id.options) {
                if (!this.getClass().getName().equals(ActivityApplicationOptions.class.getName())) {
                    Intent intent = new Intent(getApplicationContext(), ActivityApplicationOptions.class);
                    startActivity(intent);
                }
            }

            if (id == R.id.aboutApp) {
                if (!this.getClass().getName().equals(AboutAppActivity.class.getName())) {
                    Intent intent = new Intent(getApplicationContext(), AboutAppActivity.class);
                    startActivity(intent);
                }
            }

            if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
                if (id == R.id.logoutAccount) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        firebaseAuth.signOut();
                        ToastManager.showToast(getApplicationContext(), "Pomyślnie wylogowano");
                        Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

}
