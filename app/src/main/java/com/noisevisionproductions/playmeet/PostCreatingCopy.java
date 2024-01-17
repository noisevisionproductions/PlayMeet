package com.noisevisionproductions.playmeet;

import java.util.UUID;

public class PostCreatingCopy {
    private final String postUuid;
    private String postId;
    private String userIdCreator;
    private String userIdSavedBy;
    private String sportType;
    private String cityName;
    private String dateTime;
    private String hourTime;
    private String skillLevel;
    private String additionalInfo;
    private Boolean savedByUser = false;

    public PostCreatingCopy() {
        this.postUuid = UUID.randomUUID().toString();
    }

    public String getPostUuid() {
        return postUuid;
    }

    public String getUserIdCreator() {
        return userIdCreator;
    }

    public void setUserIdCreator(String userIdCreator) {
        this.userIdCreator = userIdCreator;
    }

    public String getUserIdSavedBy() {
        return userIdSavedBy;
    }

    public void setUserIdSavedBy(String userIdSavedBy) {
        this.userIdSavedBy = userIdSavedBy;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
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
        return savedByUser;
    }

    public void setSavedByUser(Boolean savedByUser) {
        this.savedByUser = savedByUser;
    }
}
