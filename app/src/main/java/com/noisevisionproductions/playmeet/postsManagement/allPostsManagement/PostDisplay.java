package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

public interface PostDisplay {
    void getUserPosts(String userId, OnPostsFetchedListener listener);

    void getRegisteredPosts(String userId, OnPostsFetchedListener listener);
}
