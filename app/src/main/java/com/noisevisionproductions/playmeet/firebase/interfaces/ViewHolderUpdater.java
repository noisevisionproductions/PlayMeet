package com.noisevisionproductions.playmeet.firebase.interfaces;

import android.content.Context;

public interface ViewHolderUpdater {
    void updatePeopleStatus(String status);

    void applyAnimation();

    void setUserAvatar(Context context, String userId);
}
