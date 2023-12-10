package com.example.zagrajmy.Chat;

import java.util.Date;

import io.realm.RealmObject;

public class ChatMessageModel extends RealmObject {
    private String userId;
    private String message;
    private Date timestamp;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String userId, String message, Date timestamp) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
