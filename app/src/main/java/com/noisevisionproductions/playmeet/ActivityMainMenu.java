package com.noisevisionproductions.playmeet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.chat.ChatRoomList;
import com.noisevisionproductions.playmeet.design.TopMenuLayout;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.postsManagement.PostCreatingLogic;
import com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.PostsOfTheGamesFragment;
import com.noisevisionproductions.playmeet.postsManagement.userPosts.PostsCreatedByUserFragment;
import com.noisevisionproductions.playmeet.userManagement.UserAccountLogic;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

import java.util.Objects;

public class ActivityMainMenu extends TopMenuLayout {
    private AppCompatButton yourPostsMenu, createPostMenu, showAllPostsMenu, chatRoomMenu, userProfileMenu, updateUserInfoBar;
    private FragmentContainerView fragmentContainerActivePosts;
    private final PostCreatingLogic postCreatingLogic = new PostCreatingLogic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setUpUIElements();

        // poruszanie sie miedzy fragmentami
        switchToUserPosts();
        switchToMainMenu();
        switchToCreatePost();
        switchToChatRoom();
        switchToUserProfile();

        //handleBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //  switchToUserInfoInputOnClick();
        checkUsersForNickname();
    }

    private void setUpUIElements() {
        yourPostsMenu = findViewById(R.id.yourPostsMenu);
        createPostMenu = findViewById(R.id.createPostMenu);
        showAllPostsMenu = findViewById(R.id.showAllPostsMenu);
        chatRoomMenu = findViewById(R.id.chatRoomMenu);
        userProfileMenu = findViewById(R.id.userProfileMenu);
        fragmentContainerActivePosts = findViewById(R.id.fragmentContainerActivePosts);

        updateUserInfoBar = findViewById(R.id.updateUserInfoBar);
        updateUserInfoBar.setOnClickListener(v -> switchToUserInfoInput());

        showAllPostsMenu.setSelected(true);
    }

    private void checkUsersForNickname() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUser.getUid());
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        String nickname = userModel.getNickname();
                        if (nickname == null || nickname.isEmpty()) {
                            updateUserInfoBar.setVisibility(View.VISIBLE);
                            switchToUserInfoInput();
                            setMarginForFragmentContainerActivePosts(60);
                        } else {
                            updateUserInfoBar.setVisibility(View.GONE);
                            setMarginForFragmentContainerActivePosts(0);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase Save Error", "Checking if logged in user has nickName " + Objects.requireNonNull(error.getMessage()));
                }
            });
        } else {
            updateUserInfoBar.setVisibility(View.GONE);
            setMarginForFragmentContainerActivePosts(0);
        }
    }

    public void switchToUserInfoInput() {
        DialogFragment dialogFragment = new ContainerForDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "my_dialog");
    }

    public void onUserInfoUpdated() {
        updateUserInfoBar.setVisibility(View.GONE);
        refreshCurrentFragment();
    }

    private void refreshCurrentFragment() {
        Intent intent = new Intent(this, ActivityMainMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setMarginForFragmentContainerActivePosts(int marginTopInDp) {
        float density = getResources().getDisplayMetrics().density;
        int marginTopInPx = (int) (marginTopInDp * density);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) fragmentContainerActivePosts.getLayoutParams();
        layoutParams.topMargin = marginTopInPx;
        fragmentContainerActivePosts.setLayoutParams(layoutParams);
    }

    private void switchToUserPosts() {
        yourPostsMenu.setOnClickListener(view -> {

            yourPostsMenu.setSelected(true);
            createPostMenu.setSelected(false);
            showAllPostsMenu.setSelected(false);
            chatRoomMenu.setSelected(false);
            userProfileMenu.setSelected(false);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerActivePosts, PostsCreatedByUserFragment.class, null).setReorderingAllowed(true).commit();
        });
    }

    private void switchToCreatePost() {

        createPostMenu.setOnClickListener(view -> {
            if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {

                yourPostsMenu.setSelected(false);
                createPostMenu.setSelected(true);
                showAllPostsMenu.setSelected(false);
                chatRoomMenu.setSelected(false);
                userProfileMenu.setSelected(false);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().setReorderingAllowed(true).addToBackStack(null).replace(R.id.fragmentContainerActivePosts, postCreatingLogic).commit();
            } else {
                ProjectUtils.showLoginSnackBar(this);
            }
        });
    }

    private void switchToMainMenu() {
        showAllPostsMenu.setOnClickListener(view -> {
            yourPostsMenu.setSelected(false);
            createPostMenu.setSelected(false);
            showAllPostsMenu.setSelected(true);
            chatRoomMenu.setSelected(false);
            userProfileMenu.setSelected(false);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerActivePosts, PostsOfTheGamesFragment.class, null).setReorderingAllowed(true).commit();
        });
    }

    private void switchToChatRoom() {
        chatRoomMenu.setOnClickListener(view -> {
            if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
                yourPostsMenu.setSelected(false);
                createPostMenu.setSelected(false);
                showAllPostsMenu.setSelected(false);
                chatRoomMenu.setSelected(true);
                userProfileMenu.setSelected(false);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragmentContainerActivePosts, ChatRoomList.class, null).setReorderingAllowed(true).commit();
            } else {
                ProjectUtils.showLoginSnackBar(this);
            }
        });
    }

    private void switchToUserProfile() {
        userProfileMenu.setOnClickListener(view -> {

            yourPostsMenu.setSelected(false);
            createPostMenu.setSelected(false);
            showAllPostsMenu.setSelected(false);
            chatRoomMenu.setSelected(false);
            userProfileMenu.setSelected(true);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragmentContainerActivePosts, UserAccountLogic.class, null).setReorderingAllowed(true).commit();
        });
    }
}