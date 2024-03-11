package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

public interface PostDisplay {
    void filterAllPosts(boolean isUserLoggedIn, String currentUserId, OnPostsFetchedListener listener);

    void getUserPosts(String userId, OnPostsFetchedListener listener);

    void getRegisteredPosts(String userId, OnPostsFetchedListener listener);
}
