package com.example.zagrajmy.PostsManagement.UserPosts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.R;

public class UsersActivePosts extends AppCompatActivity {
    private AppCompatButton buttonYourPosts;
    private AppCompatButton buttonSavedPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_posts);

        buttonYourPosts = findViewById(R.id.postsAddedByYou);
        buttonSavedPosts = findViewById(R.id.postsSavedByYou);

        buttonYourPosts.setSelected(true);

        switchToUserPosts();
        switchToFavoritePosts();
        mainMenuButton();
    }

    public void switchToUserPosts() {
        buttonYourPosts.setOnClickListener(view -> {

            buttonYourPosts.setSelected(true);
            buttonSavedPosts.setSelected(false);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerActivePosts, PostsCreatedByUserFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        });
    }

    public void switchToFavoritePosts() {
        buttonSavedPosts.setOnClickListener(view -> {
            buttonYourPosts.setSelected(true);
            buttonSavedPosts.setSelected(false);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerActivePosts, PostsFavoriteByUserFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        });
    }

    public void mainMenuButton() {
        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
    }

}