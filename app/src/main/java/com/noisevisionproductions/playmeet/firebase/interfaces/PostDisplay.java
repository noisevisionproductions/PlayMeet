package com.noisevisionproductions.playmeet.firebase.interfaces;

public interface PostDisplay {
    void getUserPosts(String userId, OnPostsFetchedListener listener);

    void getRegisteredPosts(String userId, OnPostsFetchedListener listener);
}
