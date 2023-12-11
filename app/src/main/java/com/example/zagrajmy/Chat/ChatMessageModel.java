package com.example.zagrajmy.Chat;

import android.os.Build;

import com.example.zagrajmy.UserManagement.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ChatMessageModel extends RealmObject {
    // private String userId;
    private long timestamp;
    private RealmList<User> users;
    private String message;

    public ChatMessageModel() {
    }

    public ChatMessageModel(RealmList<User> users, String message, long timestamp) {
        this.users = users;
        this.message = message;
        this.timestamp = timestamp;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(RealmList<User> users) {
        this.users = users;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return formatDate();
    }

    public String formatDate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime timestampAsDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            return timestampAsDateTime.format(dateTimeFormatter);
        } else {
            return null;
        }
    }

    public void setTimestamp(LocalDateTime timestamp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.timestamp = timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
    }
}
