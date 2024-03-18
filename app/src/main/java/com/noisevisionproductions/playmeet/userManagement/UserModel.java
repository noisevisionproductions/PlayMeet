package com.noisevisionproductions.playmeet.userManagement;

public class UserModel {
    private String fcmToken;
    private String userId;
    private String nickname;
    private String name;
    private String age;
    private String gender;
    private String location;
    private String aboutMe;
    private String avatar;
    private int joinedPostsCount = 0; // śledzi liczbę postów, do których dołączył użytkownik

    public UserModel() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getJoinedPostsCount() {
        return joinedPostsCount;
    }

    public void setJoinedPostsCount(int joinedPostsCount) {
        this.joinedPostsCount = joinedPostsCount;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
