package com.noisevisionproductions.playmeet.firebase.interfaces;

import com.noisevisionproductions.playmeet.PostModel;

public interface PostRepository {
    void addPost(PostModel postModel, OnPostCreatedListener listener);

    void updatePost(String postId, PostModel postModel, OnCompletionListener listener);

    void getPost(String postId, PostCompletionListenerList listener);

    void deleteUserPost(String postId, PostModel postModel, OnCompletionListener listener);

    void registerUserToPost(String postId, String userId, OnCompletionListener listener);

    void removeUserFromRegistration(String postId, String userId, OnCompletionListener listener);

    void deleteAllUserPosts(String userId, OnCompletionListener listener, Runnable onComplete);

    void deleteAllUserRegistrationsAndUpdatePosts(String userId, OnCompletionListener listener);
}
