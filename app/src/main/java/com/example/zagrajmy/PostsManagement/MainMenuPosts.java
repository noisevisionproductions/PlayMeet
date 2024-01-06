package com.example.zagrajmy.PostsManagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.zagrajmy.Chat.ChatRoomList;
import com.example.zagrajmy.Realm.RealmDataManager;
import com.example.zagrajmy.FirstSetup.ContainerForDialogFragment;
import com.example.zagrajmy.Design.SidePanelBaseActivity;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.Realm.RealmAuthenticationManager;
import com.example.zagrajmy.PostsManagement.UserPosts.PostsCreatedByUserFragment;
import com.example.zagrajmy.PostsManagement.UserPosts.PostsSavedByUserFragment;
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.UserModel;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class MainMenuPosts extends SidePanelBaseActivity {
    private AppCompatButton yourPostsMenu, savedPostsMenu, showAllPostsMenu, chatRoomMenu, updateUserInfoBar;
    private RealmAuthenticationManager authenticationManager;
    private View appVersionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setUpUIElements();

        // zaladowanie panelu bocznego
        setupDrawerLayout();
        setupNavigationView();

        // poruszanie sie miedzy fragmentami
        switchToUserPosts();
        switchToMainMenu();
        switchToFavoritePosts();
        switchToChatRoom();

        switchToUserInfoInputOnClick();
        checkUsers();
    }

    public void setUpUIElements() {
        authenticationManager = new RealmAuthenticationManager();

        yourPostsMenu = findViewById(R.id.yourPostsMenu);
        savedPostsMenu = findViewById(R.id.savedPostsMenu);
        showAllPostsMenu = findViewById(R.id.showAllPostsMenu);
        chatRoomMenu = findViewById(R.id.chatRoomMenu);

        updateUserInfoBar = findViewById(R.id.updateUserInfoBar);
        appVersionInfo = findViewById(R.id.appVersionInfo);

        showAllPostsMenu.setSelected(true);
    }

    public void checkUsers() {
        App app = RealmAppConfig.getApp();
        User currentUser = app.currentUser();
        RealmDataManager realmDataManager = RealmDataManager.getInstance();

        if (authenticationManager.isUserLoggedIn()) {
            try (Realm realm = Realm.getDefaultInstance()) {
                if (currentUser != null) {
                    UserModel user = realm.where(UserModel.class)
                            .equalTo("userId", currentUser.getId())
                            .findFirst();
                    if (user != null) {
                        if (user.getNickName() == null) {
                            updateUserInfoBar.setVisibility(View.VISIBLE);
                            appVersionInfo.setVisibility(View.GONE);

                            switchToUserInfoInput();
                        } else {
                            updateUserInfoBar.setVisibility(View.GONE);
                            appVersionInfo.setVisibility(View.VISIBLE);
                        }
                    } else {
                        UserModel userModelClass = new UserModel();
                        userModelClass.setUserId(currentUser.getId());
                        realmDataManager.addUser(userModelClass);
                    }
                }
            }
        } else {
            updateUserInfoBar.setVisibility(View.GONE);
            appVersionInfo.setVisibility(View.VISIBLE);
        }
    }

    public void switchToUserInfoInput() {
        DialogFragment dialogFragment = new ContainerForDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "my_dialog");
    }

    public void switchToUserInfoInputOnClick() {
        updateUserInfoBar.setOnClickListener(v -> switchToUserInfoInput());
    }

    public void onUserInfoUpdated() {
        updateUserInfoBar.setVisibility(View.GONE);
        appVersionInfo.setVisibility(View.VISIBLE);
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
            if (authenticationManager.isUserLoggedIn()) {

                yourPostsMenu.setSelected(false);
                savedPostsMenu.setSelected(true);
                showAllPostsMenu.setSelected(false);
                chatRoomMenu.setSelected(false);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerActivePosts, PostsSavedByUserFragment.class, null)
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
            if (authenticationManager.isUserLoggedIn()) {
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
}