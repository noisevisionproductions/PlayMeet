package com.noisevisionproductions.playmeet;

import androidx.annotation.NonNull;

import com.noisevisionproductions.playmeet.postsManagement.PostInfo;

public class PostCreating implements PostInfo {
    private String postId;
    private Boolean createdByUser = false;
    private Boolean isActivityFull = false;
    private String userId;
    private String sportType;
    private String cityName;
    private String dateTime;
    private String hourTime;
    private String skillLevel;
    private String peopleStatus;
    private int howManyPeopleNeeded;
    private String additionalInfo;
    private int signedUpCount = 0;

    public PostCreating() {
    }

    public Boolean getCreatedByUser() {
        return createdByUser;
    }

    public PostCreating(String postId, String sportType, String cityName, String additionalInfo, String skillLevel, String dateTime, String hourTime) {
        this.postId = postId;
        this.sportType = sportType;
        this.cityName = cityName;
        this.additionalInfo = additionalInfo;
        this.skillLevel = skillLevel;
        this.dateTime = dateTime;
        this.hourTime = hourTime;
    }

    @NonNull
    public PostCreating copyOfAllPosts() {
        PostCreating copyOfAllPosts = new PostCreating();
        copyOfAllPosts.setPostId(postId);
        copyOfAllPosts.setIsCreatedByUser(createdByUser);
        copyOfAllPosts.setActivityFull(isActivityFull);
        copyOfAllPosts.setUserId(userId);
        copyOfAllPosts.setSportType(sportType);
        copyOfAllPosts.setCityName(cityName);
        copyOfAllPosts.setDateTime(dateTime);
        copyOfAllPosts.setHourTime(hourTime);
        copyOfAllPosts.setSkillLevel(skillLevel);
        copyOfAllPosts.setAdditionalInfo(additionalInfo);
        return copyOfAllPosts;
    }


    public int getPeopleSignedUp() {
        return signedUpCount;
    }

    public void userSignedUp() {
        signedUpCount++;
        updatePeopleStatus();
    }

    public void deleteSignedUpUser() {
        if (signedUpCount > 0) {
            signedUpCount--;
            updatePeopleStatus();
        }
    }

    public void updatePeopleStatus() {
        peopleStatus = getPeopleSignedUp() + "/" + howManyPeopleNeeded;
    }

    @NonNull
    public String getPeopleStatus() {
        return peopleStatus;
    }

    public int getHowManyPeopleNeeded() {
        return howManyPeopleNeeded;
    }

    public void setHowManyPeopleNeeded(int howManyPeopleNeeded) {
        this.howManyPeopleNeeded = howManyPeopleNeeded;
    }

    public void setIsCreatedByUser(Boolean createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Boolean getActivityFull() {
        return isActivityFull;
    }

    public void setActivityFull(Boolean activityFull) {
        isActivityFull = activityFull;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
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


}

