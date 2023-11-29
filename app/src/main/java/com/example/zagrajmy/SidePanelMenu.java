package com.example.zagrajmy;

import android.content.Intent;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.LoginRegister.LoginAndRegisterActivity;
import com.example.zagrajmy.PostsManagement.PostsOfTheGames;
import com.example.zagrajmy.PostsManagement.UserPosts.UsersActivePosts;
import com.example.zagrajmy.UserManagement.UserAccountLogic;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class SidePanelMenu {
    private MainMenu mainMenu;
    private PostsOfTheGames postsOfTheGames;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public SidePanelMenu() {
    }

    public SidePanelMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        this.drawerLayout = mainMenu.getDrawerLayout();
        this.navigationView = mainMenu.getNavigationView();
    }

    public SidePanelMenu(PostsOfTheGames postsOfTheGames) {
        this.postsOfTheGames = postsOfTheGames;
        this.drawerLayout = postsOfTheGames.getDrawerLayout();
        this.navigationView = postsOfTheGames.getNavigationView();
    }

    public void manageDrawerButtonsNew() {
        this.navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.wylogujKonto) {
                FirebaseAuth.getInstance().signOut();
                RealmDatabaseManagement realmDatabaseManagement = new RealmDatabaseManagement();
                realmDatabaseManagement.closeRealmDatabase();
                Intent intent = new Intent(postsOfTheGames.getApplicationContext(), LoginAndRegisterActivity.class);
                postsOfTheGames.startActivity(intent);
                postsOfTheGames.finish();
            }
            if (id == R.id.userProfile) {
                Intent intent = new Intent(postsOfTheGames.getApplicationContext(), UserAccountLogic.class);
                postsOfTheGames.startActivity(intent);
                postsOfTheGames.finish();
            }

            if (id == R.id.mojaAktywnosc) {
                Intent intent = new Intent(postsOfTheGames.getApplicationContext(), UsersActivePosts.class);
                postsOfTheGames.startActivity(intent);
                postsOfTheGames.finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    public void manageDrawerButtons() {
        this.navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.wylogujKonto) {
                FirebaseAuth.getInstance().signOut();
                RealmDatabaseManagement realmDatabaseManagement = new RealmDatabaseManagement();
                realmDatabaseManagement.closeRealmDatabase();
                Intent intent = new Intent(mainMenu.getApplicationContext(), LoginAndRegisterActivity.class);
                mainMenu.startActivity(intent);
                mainMenu.finish();
            }
            if (id == R.id.userProfile) {
                Intent intent = new Intent(mainMenu.getApplicationContext(), UserAccountLogic.class);
                mainMenu.startActivity(intent);
                mainMenu.finish();
            }

            if (id == R.id.mojaAktywnosc) {
                Intent intent = new Intent(mainMenu.getApplicationContext(), UsersActivePosts.class);
                mainMenu.startActivity(intent);
                mainMenu.finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}
