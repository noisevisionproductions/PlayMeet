package com.noisevisionproductions.playmeet;

public class PostCreating {
    private String postId;
    private boolean isCreatedByUser = false;
    private boolean isPostSavedByUser = false;
    private boolean isExtraInfoOpen = false;
    private String userId;
    private String sportType;
    private String cityName;
    private String dateTime;
    private String hourTime;
    private String skillLevel;
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
        copyOfAllPosts.setIsCreatedByUser(isCreatedByUser);
        copyOfAllPosts.setPostSavedByUser(isPostSavedByUser);
        copyOfAllPosts.setExtraInfoOpen(isExtraInfoOpen);
        copyOfAllPosts.setUserId(userId);
        copyOfAllPosts.setSportType(sportType);
        copyOfAllPosts.setCityName(cityName);
        copyOfAllPosts.setDateTime(dateTime);
        copyOfAllPosts.setHourTime(hourTime);
        copyOfAllPosts.setSkillLevel(skillLevel);
        copyOfAllPosts.setAdditionalInfo(additionalInfo);
        return copyOfAllPosts;
    }

    public void setPostSavedByUser(Boolean postSavedByUser) {
        isPostSavedByUser = postSavedByUser;
    }

    public void setIsCreatedByUser(Boolean isCreatedByUser) {
        this.isCreatedByUser = isCreatedByUser;
    }

    public boolean isCreatedByUser() {
        return isCreatedByUser;
    }

    public void setExtraInfoOpen(boolean extraInfoOpen) {
        isExtraInfoOpen = extraInfoOpen;
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


