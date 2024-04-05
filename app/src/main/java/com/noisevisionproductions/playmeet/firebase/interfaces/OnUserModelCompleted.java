package com.noisevisionproductions.playmeet.firebase.interfaces;

import com.noisevisionproductions.playmeet.userManagement.UserModel;

public interface OnUserModelCompleted {
    void onSuccess(UserModel userModel);

    void onFailure(Exception e);
}
