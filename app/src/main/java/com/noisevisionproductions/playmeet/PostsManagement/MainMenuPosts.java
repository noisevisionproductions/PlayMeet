package com.noisevisionproductions.playmeet.PostsManagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.Chat.ChatRoomList;
import com.noisevisionproductions.playmeet.Design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.Firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.FirstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.LoginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement.MyBottomSheetFragment;
import com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement.PostsOfTheGamesFragment;
import com.noisevisionproductions.playmeet.PostsManagement.UserPosts.PostsCreatedByUserFragment;
import com.noisevisionproductions.playmeet.PostsManagement.UserPosts.PostsSavedByUserFragment;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;
import com.noisevisionproductions.playmeet.Utilities.OpinionFromUser;
import com.noisevisionproductions.playmeet.Utilities.ProjectUtils;

import java.util.Objects;

public class MainMenuPosts extends SidePanelBaseActivity implements MyBottomSheetFragment.OnDataPass {
    private AppCompatButton yourPostsMenu, savedPostsMenu, showAllPostsMenu, chatRoomMenu, updateUserInfoBar, sendOpinionButton;

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

        sendOpinionButtonHandle();

        handleBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUsersForNickname();
        switchToUserInfoInputOnClick();
    }

    @Override
    public void onDataPass(String data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentBottomSheet);
        if (fragment instanceof PostsOfTheGamesFragment.OnDataReceived) {
            ((PostsOfTheGamesFragment.OnDataReceived) fragment).onDataReceived(data);
        }
    }

    private void setUpUIElements() {
        yourPostsMenu = findViewById(R.id.yourPostsMenu);
        savedPostsMenu = findViewById(R.id.savedPostsMenu);
        showAllPostsMenu = findViewById(R.id.showAllPostsMenu);
        chatRoomMenu = findViewById(R.id.chatRoomMenu);

        updateUserInfoBar = findViewById(R.id.updateUserInfoBar);
        sendOpinionButton = findViewById(R.id.sendOpinionButton);

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
                            sendOpinionButton.setVisibility(View.GONE);
                            switchToUserInfoInput();
                        } else {
                            updateUserInfoBar.setVisibility(View.GONE);
                            sendOpinionButton.setVisibility(View.VISIBLE);
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
            sendOpinionButton.setVisibility(View.VISIBLE);
        }
    }

    public void switchToUserInfoInput() {
        DialogFragment dialogFragment = new ContainerForDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "my_dialog");
    }

    public void switchToOpinionLayout() {
        Intent intent = new Intent(this, OpinionFromUser.class);
        startActivity(intent);
    }


    public void switchToUserInfoInputOnClick() {
        updateUserInfoBar.setOnClickListener(v -> switchToUserInfoInput());
    }

    public void sendOpinionButtonHandle() {
        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
            sendOpinionButton.setOnClickListener(v -> switchToOpinionLayout());
        } else {
            ProjectUtils.showLoginSnackBar(this);
        }
    }

    public void onUserInfoUpdated() {
        updateUserInfoBar.setVisibility(View.GONE);
        sendOpinionButton.setVisibility(View.VISIBLE);
    }

    private void switchToUserPosts() {
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

    private void switchToFavoritePosts() {
        savedPostsMenu.setOnClickListener(view -> {
            if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {

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
                ProjectUtils.showLoginSnackBar(this);
            }
        });
    }

    private void switchToMainMenu() {
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

    private void switchToChatRoom() {
        chatRoomMenu.setOnClickListener(view -> {
            if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
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
                ProjectUtils.showLoginSnackBar(this);
            }
        });
    }

    private void handleBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Wyjście")
                .setMessage("Wylogować, czy zamknąć aplikację?")
                .setPositiveButton("Wyloguj się", (dialog, which) -> {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        firebaseAuth.signOut();
                        Toast.makeText(getApplicationContext(), "Pomyślnie wylogowano", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Wyjście", (dialog, which) -> finishAffinity())
                .setNeutralButton("Anuluj", null)
                .show();
    }
}