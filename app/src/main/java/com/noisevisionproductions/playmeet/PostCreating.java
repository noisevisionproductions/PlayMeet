package com.noisevisionproductions.playmeet;

import java.util.Objects;

public class PostCreating {
    private String postId;
    private Boolean createdByUser = false;
    private Boolean savedByUser = false;
    private String userId;
    private String sportType;
    private String cityName;
    private String dateTime;
    private String hourTime;
    private String skillLevel;
    private int howManyPeopleNeeded;
    private int peopleSignedUp = 0;
    private String additionalInfo;

    public PostCreating() {
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

    public PostCreating copyOfAllPosts() {
        PostCreating copyOfAllPosts = new PostCreating();
        copyOfAllPosts.setPostId(postId);
        copyOfAllPosts.setIsCreatedByUser(createdByUser);
        copyOfAllPosts.setSavedByUser(savedByUser);
        copyOfAllPosts.setUserId(userId);
        copyOfAllPosts.setSportType(sportType);
        copyOfAllPosts.setCityName(cityName);
        copyOfAllPosts.setDateTime(dateTime);
        copyOfAllPosts.setHourTime(hourTime);
        copyOfAllPosts.setSkillLevel(skillLevel);
        copyOfAllPosts.setAdditionalInfo(additionalInfo);
        return copyOfAllPosts;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PostCreating that = (PostCreating) obj;
        return Objects.equals(postId, that.postId);
    }

    public void setSavedByUser(Boolean savedByUser) {
        this.savedByUser = savedByUser;
    }

    public void setIsCreatedByUser(Boolean createdByUser) {
        this.createdByUser = createdByUser;
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

    public int getHowManyPeopleNeeded() {
        return howManyPeopleNeeded;
    }
    public int getPeopleSignedUp(){
        return peopleSignedUp;
    }

    public void setHowManyPeopleNeeded(int howManyPeopleNeeded) {
        this.howManyPeopleNeeded = howManyPeopleNeeded;
    }

    public void userSignedUp() {
        peopleSignedUp++;
    }

    public void deleteSignedUpUser() {
        if (peopleSignedUp > 0) {
            peopleSignedUp -= 1;
        }
    }

    public String getPeopleStatus() {
        return peopleSignedUp + "/" + howManyPeopleNeeded;
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


