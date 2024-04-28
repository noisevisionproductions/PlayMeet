package com.noisevisionproductions.playmeet.userManagement.userProfile;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

public class OpenUserProfile {

    public static void openUserProfile(String userId, FragmentManager fragmentManager) {
        if (fragmentManager.findFragmentByTag("userProfile") == null) {
            UserProfile userProfile = new UserProfile();
            Bundle args = new Bundle();
            args.putString(ConstantUserId.USER_ID_KEY, userId);
            userProfile.setArguments(args);

            userProfile.show(fragmentManager, "userProfile");
        }
    }
}
