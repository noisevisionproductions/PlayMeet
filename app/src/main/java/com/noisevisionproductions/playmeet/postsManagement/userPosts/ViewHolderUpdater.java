package com.noisevisionproductions.playmeet.postsManagement.userPosts;

import android.content.Context;

public interface ViewHolderUpdater {
    void updatePeopleStatus(String status);

    void applyAnimation();

    void setUserAvatar(Context context, String userId);
}
