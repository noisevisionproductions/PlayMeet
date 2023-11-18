package com.example.zagrajmy.Design;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.zagrajmy.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

// TODO : stworzyc uniwersalna klase do panelu bocznego, aby byla dostepna w innych widokach
public class SidePanelActivity extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected NavigationView navigationView;

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }
    public NavigationView getNavigationView() {
        return navigationView;
    }

    protected int getLayoutId(){
        return R.layout.activity_main;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        setupDrawerLayout();
      //  SidePanelMenu sidePanelMenu = new SidePanelMenu(this);
     //   sidePanelMenu.manageDrawerButtons();
    }

    protected void setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationViewSidePanel);
    }
}
