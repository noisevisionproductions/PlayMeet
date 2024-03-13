package com.noisevisionproductions.playmeet.firebase.interfaces;

public interface OnJoinedPostsCountListener {
    void onCountReceived(int count);

    void onFailure(Exception e);
}
