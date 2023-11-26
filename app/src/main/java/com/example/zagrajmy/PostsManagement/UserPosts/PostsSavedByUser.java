package com.example.zagrajmy.PostsManagement.UserPosts;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PostsSavedByUser extends RealmObject {

    @PrimaryKey
    private int id;
    private String userId;
    private int postId;
    private boolean isPostSavedByUser = false;
    private String sportType;
    private String cityName;
    private String dateTime;
    private String hourTime;
    private String skillLevel;
    private String additionalInfo;
    private String buttonColor;
    private String buttonText;

    public void setButtonColorAndText(String buttonColor, String buttonText){
        this.buttonColor = buttonColor;
        this.buttonText = buttonText;
    }

    public void setIsPostSavedByUser(boolean isPostSavedByUser){
        this.isPostSavedByUser = isPostSavedByUser;
    }
    public boolean isPostSavedByUser() {
        return isPostSavedByUser;
    }

    public String getButtonColor(){
        return buttonColor;
    }

    public String getButtonText(){
        return buttonText;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
