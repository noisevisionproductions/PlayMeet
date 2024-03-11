package com.noisevisionproductions.playmeet.userManagement;

public interface OnCompletionListener {
    void onSuccess();

    void onFailure(Exception e);
}
