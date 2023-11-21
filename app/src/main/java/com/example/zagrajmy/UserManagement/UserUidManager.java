package com.example.zagrajmy.UserManagement;

public class UserUidManager {
    private static UserUidManager instance;
    private User user;

    private UserUidManager() {

    }
    public static UserUidManager getInstance() {
        if (instance == null) {
            instance = new UserUidManager();
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
