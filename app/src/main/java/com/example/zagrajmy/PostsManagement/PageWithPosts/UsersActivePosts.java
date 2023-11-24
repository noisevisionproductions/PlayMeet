package com.example.zagrajmy.PostsManagement.PageWithPosts;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.R;

public class UsersActivePosts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_posts);

        switchToUserPosts();
        switchToFavoritePosts();
        mainMenuButton();
    }

    public void switchToUserPosts() {
        Button buttonLogin = findViewById(R.id.postsAddedByYou);

        buttonLogin.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerActivePosts, PostsCreatedByUserFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        });
    }

    public void switchToFavoritePosts() {
        Button buttonRegister = findViewById(R.id.postsSavedByYou);

        buttonRegister.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerActivePosts, PostsFavoriteByUserFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        });
    }

    public void mainMenuButton(){
        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
    }

}