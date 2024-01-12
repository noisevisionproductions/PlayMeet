package com.noisevisionproductions.playmeet.PostsManagement;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noisevisionproductions.playmeet.Chat.ChatRoomList;
import com.noisevisionproductions.playmeet.Design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.FirstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.LoginRegister.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement.MyBottomSheetFragment;
import com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement.PostsOfTheGamesFragment;
import com.noisevisionproductions.playmeet.PostsManagement.UserPosts.PostsCreatedByUserFragment;
import com.noisevisionproductions.playmeet.PostsManagement.UserPosts.PostsSavedByUserFragment;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.Utilities.NavigationUtils;

public class MainMenuPosts extends SidePanelBaseActivity implements MyBottomSheetFragment.OnDataPass {
    private AppCompatButton yourPostsMenu, savedPostsMenu, showAllPostsMenu, chatRoomMenu, updateUserInfoBar;
    private FirebaseAuthManager authenticationManager;
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


    @Override
    public void onDataPass(String data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentBottomSheet);
        if (fragment instanceof PostsOfTheGamesFragment.OnDataReceived) {
            ((PostsOfTheGamesFragment.OnDataReceived) fragment).onDataReceived(data);
        }
    }

    public void setUpUIElements() {
        authenticationManager = new FirebaseAuthManager();

        yourPostsMenu = findViewById(R.id.yourPostsMenu);
        savedPostsMenu = findViewById(R.id.savedPostsMenu);
        showAllPostsMenu = findViewById(R.id.showAllPostsMenu);
        chatRoomMenu = findViewById(R.id.chatRoomMenu);

        updateUserInfoBar = findViewById(R.id.updateUserInfoBar);
        appVersionInfo = findViewById(R.id.appVersionInfo);

        showAllPostsMenu.setSelected(true);
    }

    public void checkUsers() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (authenticationManager.isUserLoggedIn()) {
            if (currentUser != null) {
                if (currentUser.getDisplayName() == null) {
                    updateUserInfoBar.setVisibility(View.VISIBLE);
                    appVersionInfo.setVisibility(View.GONE);

                    switchToUserInfoInput();
                } else {
                    updateUserInfoBar.setVisibility(View.GONE);
                    appVersionInfo.setVisibility(View.VISIBLE);
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
                NavigationUtils.showOnlyForLoggedUserMessage(findViewById(android.R.id.content));
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
                NavigationUtils.showOnlyForLoggedUserMessage(findViewById(android.R.id.content));
            }
        });
    }


}