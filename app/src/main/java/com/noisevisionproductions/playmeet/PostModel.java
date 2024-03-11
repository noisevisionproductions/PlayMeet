package com.noisevisionproductions.playmeet;

import androidx.annotation.NonNull;

import com.google.firebase.database.PropertyName;
import com.noisevisionproductions.playmeet.postsManagement.PostInfo;

public class PostModel implements PostInfo {
    @PropertyName("postId")
    private String postId;
    @PropertyName("createdByUser")
    private Boolean createdByUser = false;
    @PropertyName("activityFull")
    private Boolean isActivityFull = false;
    @PropertyName("userId")
    private String userId;
    @PropertyName("sportTpe")
    private String sportType;
    @PropertyName("cityName")
    private String cityName;
    @PropertyName("dateTime")
    private String dateTime;
    @PropertyName("hourTime")
    private String hourTime;
    @PropertyName("skillLevel")
    private String skillLevel;
    @PropertyName("peopleStatus")
    private String peopleStatus;
    @PropertyName("howManyPeopleNeeded")
    private int howManyPeopleNeeded;
    @PropertyName("additionalInfo")
    private String additionalInfo;
    @PropertyName("signedUpCount")
    private int signedUpCount = 0;

    public PostModel() {
    }

    @NonNull
    public PostModel copyOfAllPosts() {
        PostModel copyOfAllPosts = new PostModel();
        // copyOfAllPosts.setPostId(postId);
        // copyOfAllPosts.getCreatedByUser(createdByUser);
        copyOfAllPosts.setActivityFull(isActivityFull);
        // copyOfAllPosts.setUserId(userId);
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
        isActivityFull = signedUpCount >= howManyPeopleNeeded;
        updatePeopleStatus();
    }

    public void deleteSignedUpUser() {
        if (signedUpCount > 0) {
            signedUpCount--;
            isActivityFull = signedUpCount >= howManyPeopleNeeded;
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

    @Override
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        updatePeopleStatus();
        this.postId = postId;
    }

    public Boolean getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(Boolean createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Boolean getActivityFull() {
        return isActivityFull;
    }

    public void setActivityFull(Boolean activityFull) {
        isActivityFull = activityFull;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    @Override
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
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

    public void setPeopleStatus(String peopleStatus) {
        this.peopleStatus = peopleStatus;
    }

    public int getHowManyPeopleNeeded() {
        return howManyPeopleNeeded;
    }

    public void setHowManyPeopleNeeded(int howManyPeopleNeeded) {
        this.howManyPeopleNeeded = howManyPeopleNeeded;
    }

    @Override
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public int getSignedUpCount() {
        return signedUpCount;
    }

    public void setSignedUpCount(int signedUpCount) {
        this.signedUpCount = signedUpCount;
    }
}

