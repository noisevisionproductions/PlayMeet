package com.noisevisionproductions.playmeet.firebase.interfaces;

import com.noisevisionproductions.playmeet.userManagement.UserModel;

import java.util.List;

public interface OnUserModelListCompleted {
    void onSuccess(List<UserModel> userList);

    void onFailure(Exception e);
}
