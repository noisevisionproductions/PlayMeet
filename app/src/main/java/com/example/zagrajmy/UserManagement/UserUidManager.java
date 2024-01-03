package com.example.zagrajmy.UserManagement;

public class UserUidManager {
    private static UserUidManager instance;
    private UserModel userModel;

    private UserUidManager() {

    }
    public static UserUidManager getInstance() {
        if (instance == null) {
            instance = new UserUidManager();
        }
        return instance;
    }

    public void setUser(UserModel userModel) {
        this.userModel = userModel;
    }

    public UserModel getUser() {
        return userModel;
    }
}
