package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.bottomSheetFragment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.chat.ChatActivity;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.interfaces.PostInfo;
import com.noisevisionproductions.playmeet.firstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

import java.util.Objects;

public class ButtonsForChatAndSignIn {

    public static void checkNicknameAndPerformAction(String currentUserId, Runnable action, FragmentManager fragmentManager) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUserId);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                if (userModel != null) {
                    String nickname = userModel.getNickname();
                    if (nickname == null || nickname.isEmpty()) {
                        DialogFragment dialogFragment = new ContainerForDialogFragment();
                        dialogFragment.show(fragmentManager, "my_dialog");
                    } else {
                        action.run();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Save Error", "Checking if logged in user has nickName " + Objects.requireNonNull(error.getMessage()));
            }
        });
    }

    public static void handleChatButtonClick(@NonNull View view, String postOwnerId, FragmentManager fragmentManager) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();

        if (firebaseHelper.getCurrentUser() != null) {
            String currentUserId = firebaseHelper.getCurrentUser().getUid();
            checkNicknameAndPerformAction(currentUserId, () -> firebaseHelper.getExistingChatRoomId(currentUserId, postOwnerId, chatRoomId -> navigateToChatRoom(view, chatRoomId)), fragmentManager);
        }
    }

    private static void navigateToChatRoom(@NonNull View view, String roomId) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        intent.putExtra("roomId", roomId);
        view.getContext().startActivity(intent);
    }

    public static void handleMoreInfoButton(@NonNull FragmentManager fragmentManager, PostInfo postCreating, @NonNull Context context) {
        if (FirebaseAuthManager.isUserLoggedIn()) {
            MyBottomSheetFragment bottomSheetFragment = MyBottomSheetFragment.newInstance(postCreating);

            bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
        } else {
            ProjectUtils.showLoginSnackBar(context);
        }
    }
}
