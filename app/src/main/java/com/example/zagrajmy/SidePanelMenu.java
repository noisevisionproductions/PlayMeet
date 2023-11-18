package com.example.zagrajmy;

import android.content.Intent;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.zagrajmy.LoginRegister.LoginAndRegisterActivity;
import com.example.zagrajmy.UserManagement.UserAccountLogic;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class SidePanelMenu {
    private MainMenu mainMenu;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public SidePanelMenu() {
    }

    public SidePanelMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        this.drawerLayout = mainMenu.getDrawerLayout();
        this.navigationView = mainMenu.getNavigationView();
    }

    public void manageDrawerButtons() {
        this.navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.wylogujKonto) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(mainMenu.getApplicationContext(), LoginAndRegisterActivity.class);
                mainMenu.startActivity(intent);
                mainMenu.finish();
            }
            if (id == R.id.userProfile) {
                Intent intent = new Intent(mainMenu.getApplicationContext(), UserAccountLogic.class);
                mainMenu.startActivity(intent);
                mainMenu.finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}
