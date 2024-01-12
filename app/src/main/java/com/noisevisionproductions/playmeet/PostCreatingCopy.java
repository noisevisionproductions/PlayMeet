package com.noisevisionproductions.playmeet;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PostCreatingCopy extends RealmObject {
    @PrimaryKey
    private String postUuid;
    private int postId;
    private String userId;
    private String sportType;
    private String cityName;
    private String dateTime;
    private String hourTime;
    private String skillLevel;
    private String additionalInfo;
    private Boolean isSavedByUser = false;


    public PostCreatingCopy() {
        this.postUuid = UUID.randomUUID().toString();
    }

    public String getPostUuid() {
        return postUuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getHourTime() {
        return hourTime;
    }

    public void setHourTime(String hourTime) {
        this.hourTime = hourTime;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Boolean getSavedByUser() {
        return isSavedByUser;
    }

    public void setSavedByUser(Boolean savedByUser) {
        isSavedByUser = savedByUser;
    }
}
