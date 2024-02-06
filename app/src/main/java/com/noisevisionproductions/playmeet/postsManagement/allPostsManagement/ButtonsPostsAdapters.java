package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.noisevisionproductions.playmeet.chat.ChatActivity;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.postsManagement.PostInfo;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

public class ButtonsPostsAdapters {

    public static void handleChatButtonClick(@NonNull View view, String postOwnerId) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            String currentUserId = firebaseHelper.getCurrentUser().getUid();
            firebaseHelper.getExistingChatRoomId(currentUserId, postOwnerId, chatRoomId -> navigateToChatRoom(view, chatRoomId));
        }
    }

    private static void navigateToChatRoom(@NonNull View view, String roomId) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        intent.putExtra("roomId", roomId);
        view.getContext().startActivity(intent);
    }

    public static void handleMoreInfoButton(@NonNull FragmentManager fragmentManager, PostInfo postCreating, @NonNull Context context) {
        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
            MyBottomSheetFragment bottomSheetFragment = MyBottomSheetFragment.newInstance(postCreating);

            //  bottomSheetFragment.setDataPass(dataPass);

            bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
        } else {
            ProjectUtils.showLoginSnackBar(context);
        }
    }
}
