package com.noisevisionproductions.playmeet;

import com.google.firebase.Timestamp;
import com.google.firebase.database.PropertyName;

public class RegistrationModel {
    @PropertyName("postId")
    private String postId;
    @PropertyName("userId")
    private String userId;
    @PropertyName("registrationDate")
    private Timestamp registrationDate;

    public RegistrationModel() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }
}
