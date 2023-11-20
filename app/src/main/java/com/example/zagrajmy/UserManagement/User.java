package com.example.zagrajmy.UserManagement;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private String nickName;
    private String userId;
    private String birthDay;
    private String gender;
    private String location;
    private String aboutMe;
    private String whenAvailable;
    private String favoriteSport;
    private String avatar;

    public User() {
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

}
