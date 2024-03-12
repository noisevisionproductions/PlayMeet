package com.noisevisionproductions.playmeet.postsManagement;

import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.userManagement.OnCompletionListener;

public interface PostRepository {
    void addPost(PostModel postModel, OnPostCreatedListener listener);

    void updatePost(String postId, PostModel postModel, OnCompletionListener listener);

    void getPost(String postId, PostCompletionListenerList listener);

    void deleteUserPost(String postId, PostModel postModel, OnCompletionListener listener);

    void removeUserFromRegistration(String postId, String userId, OnCompletionListener listener);

    void registerUserToPost(String postId, String userId, OnCompletionListener listener);

    void deleteAllUserPosts(String userId, OnCompletionListener listener, Runnable onComplete);

    void deleteAllUserRegistrationsAndUpdatePosts(String userId, OnCompletionListener listener);
}
