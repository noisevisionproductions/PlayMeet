package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;

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
import com.noisevisionproductions.playmeet.Chat.ChatActivity;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.Firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.FirstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;
import com.noisevisionproductions.playmeet.Utilities.ProjectUtils;

import java.util.Objects;

public class ChatButtonHandler {

    public static void handleChatButtonClick(View view, String postOwnerId, FragmentManager fragmentManager) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        String currentUserId = firebaseHelper.getCurrentUser().getUid();
        firebaseHelper.getExistingChatRoomId(currentUserId, postOwnerId, chatRoomId -> navigateToChatRoom(view, chatRoomId));
    }

    private static void navigateToChatRoom(View view, String roomId) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        intent.putExtra("roomId", roomId);
        view.getContext().startActivity(intent);
    }

    public static void handleMoreInfoButton(FragmentManager fragmentManager, PostCreating postCreating, MyBottomSheetFragment.OnDataPass dataPass, Context context) {
        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
            MyBottomSheetFragment bottomSheetFragment = MyBottomSheetFragment.newInstance(postCreating);

            bottomSheetFragment.setDataPass(dataPass);

            bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
        } else {
            ProjectUtils.showLoginSnackBar(context);
        }
    }
}
