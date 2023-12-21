package com.example.zagrajmy.PostsManagement;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import com.example.zagrajmy.Chat.ChatRoomList;
import com.example.zagrajmy.Design.SidePanelBaseActivity;
import com.example.zagrajmy.LoginRegister.AuthenticationManager;
import com.example.zagrajmy.PostsManagement.UserPosts.PostsCreatedByUserFragment;
import com.example.zagrajmy.PostsManagement.UserPosts.PostsFavoriteByUserFragment;
import com.example.zagrajmy.R;

public class MainMenuPosts extends SidePanelBaseActivity {
    private AppCompatButton yourPostsMenu, savedPostsMenu, showAllPostsMenu, chatRoomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);

        setupDrawerLayout();
        setupNavigationView();

        yourPostsMenu = findViewById(R.id.yourPostsMenu);
        savedPostsMenu = findViewById(R.id.savedPostsMenu);
        showAllPostsMenu = findViewById(R.id.showAllPostsMenu);
        chatRoomMenu = findViewById(R.id.chatRoomMenu);

        showAllPostsMenu.setSelected(true);

        switchToUserPosts();
        switchToMainMenu();
        switchToFavoritePosts();
        switchToChatRoom();
    }

    public void switchToUserPosts() {
        yourPostsMenu.setOnClickListener(view -> {

            yourPostsMenu.setSelected(true);
            savedPostsMenu.setSelected(false);
            showAllPostsMenu.setSelected(false);
            chatRoomMenu.setSelected(false);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerActivePosts, PostsCreatedByUserFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();

        });
    }

    public void switchToFavoritePosts() {

        savedPostsMenu.setOnClickListener(view -> {
            if (AuthenticationManager.isUserLoggedIn()) {

                yourPostsMenu.setSelected(false);
                savedPostsMenu.setSelected(true);
                showAllPostsMenu.setSelected(false);
                chatRoomMenu.setSelected(false);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerActivePosts, PostsFavoriteByUserFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            } else {
                Toast.makeText(getApplicationContext().getApplicationContext(), "Dostępne jedynie dla zalogowanych użytkowników!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void switchToMainMenu() {
        showAllPostsMenu.setOnClickListener(view -> {
            yourPostsMenu.setSelected(false);
            savedPostsMenu.setSelected(false);
            showAllPostsMenu.setSelected(true);
            chatRoomMenu.setSelected(false);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerActivePosts, PostsOfTheGamesFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        });
    }

    public void switchToChatRoom() {
        chatRoomMenu.setOnClickListener(view -> {
            if (AuthenticationManager.isUserLoggedIn()) {
                yourPostsMenu.setSelected(false);
                savedPostsMenu.setSelected(false);
                showAllPostsMenu.setSelected(false);
                chatRoomMenu.setSelected(true);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerActivePosts, ChatRoomList.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            } else {
                Toast.makeText(getApplicationContext().getApplicationContext(), "Dostępne jedynie dla zalogowanych użytkowników!", Toast.LENGTH_SHORT).show();
            }

        });
    }
 /*   public void mainMenuButton() {
        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
    }*/

}