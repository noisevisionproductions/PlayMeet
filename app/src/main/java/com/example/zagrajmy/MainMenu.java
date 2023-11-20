package com.example.zagrajmy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.zagrajmy.PostsManagement.PageWithPosts.UsersActivePosts;
import com.example.zagrajmy.PostsManagement.PostCreatingLogic;
import com.example.zagrajmy.PostsManagement.PostsOfTheGames;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainMenu extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private FirebaseUser user;

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    private void setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationViewSidePanel);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        greetNickname();

        setupDrawerLayout();

        SidePanelMenu sidePanelMenu = new SidePanelMenu(this);
        sidePanelMenu.manageDrawerButtons();

        findPlayerButtonHandle();
        searchGamesButtonHandle();
        myActivityButton();
    }


    @Override
    public void onResume() {
        super.onResume();
        greetNickname();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


   /* @Override
    protected void onDestroy() {  // pomaga w testowaniu aplikacji
        super.onDestroy();

        if (user != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }*/

    public void findPlayerButtonHandle() {
        AppCompatButton createNewPostButton = findViewById(R.id.createNewPostButton);

        createNewPostButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), PostCreatingLogic.class);
            startActivity(intent);
            finish();
        });
    }

    public void searchGamesButtonHandle() {
        AppCompatButton findPlayer = findViewById(R.id.findPlayerButton);

        findPlayer.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), PostsOfTheGames.class);
            startActivity(intent);
            finish();
        });
    }

    public void myActivityButton(){
        AppCompatButton myActivity = findViewById(R.id.myactivity);

        myActivity.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), UsersActivePosts.class);
            startActivity(intent);
            finish();
        });
    }

    public void greetNickname() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String nick = user.getDisplayName();
            if (nick != null) {
                AppCompatTextView displayNickname = findViewById(R.id.nickname);
                displayNickname.setText(user.getUid());
            }
        }
    }

}
