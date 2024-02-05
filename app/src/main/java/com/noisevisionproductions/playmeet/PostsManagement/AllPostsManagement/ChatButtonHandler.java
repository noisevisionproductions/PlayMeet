package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.noisevisionproductions.playmeet.Chat.ChatActivity;
import com.noisevisionproductions.playmeet.Firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.PostsManagement.PostInfo;
import com.noisevisionproductions.playmeet.Utilities.ProjectUtils;

public class ChatButtonHandler {

    public static void handleChatButtonClick(View view, String postOwnerId) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        String currentUserId = firebaseHelper.getCurrentUser().getUid();
        firebaseHelper.getExistingChatRoomId(currentUserId, postOwnerId, chatRoomId -> navigateToChatRoom(view, chatRoomId));
    }

    private static void navigateToChatRoom(View view, String roomId) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        intent.putExtra("roomId", roomId);
        view.getContext().startActivity(intent);
    }

    public static void handleMoreInfoButton(FragmentManager fragmentManager, PostInfo postCreating, Context context) {
        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
            MyBottomSheetFragment bottomSheetFragment = MyBottomSheetFragment.newInstance(postCreating);

          //  bottomSheetFragment.setDataPass(dataPass);

            bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
        } else {
            ProjectUtils.showLoginSnackBar(context);
        }
    }
}
