package com.example.zagrajmy.UserManagement;

import io.realm.RealmObject;

public class User extends RealmObject {
    private String name;
    private String birthDay;
    private String gender;
    private String location;
    private String aboutMe;
    private String whenAvailable;
    private String favoriteSport;
    private String avatar;

    public User() {
    }

}
