package com.noisevisionproductions.playmeet.userManagement;


import java.util.Map;

public interface UserRepository {
    void addUser(UserModel user, OnCompletionListener listener);

    void getUser(String userId, OnResultListener<UserModel> listener);

    void updateUser(String userId, Map<String, Object> updates, OnCompletionListener listener);

    void deleteUser(String userId, OnCompletionListener listener);

    void updateJoinedPostsCount(String userId, int count, OnCompletionListener listener);

}
