package com.noisevisionproductions.playmeet.firebase.interfaces;


import com.noisevisionproductions.playmeet.userManagement.UserModel;

import java.util.Map;

public interface UserRepository {
    void addUser(UserModel user, OnCompletionListener listener);

    void getUser(String userId, OnResultListener listener);

    void updateUser(String userId, Map<String, Object> updates, OnCompletionListener listener);

    void deleteUser(String userId, OnCompletionListener listener);

    void getJoinedPostsCount(String userId, OnJoinedPostsCountListener listener);

    void incrementJoinedPostsCount(String userId, OnCompletionListener listener);

    void decrementJoinedPostsCount(String userId, OnCompletionListener listener);
}
