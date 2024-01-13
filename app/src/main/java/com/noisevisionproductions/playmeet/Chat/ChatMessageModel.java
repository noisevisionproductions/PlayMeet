package com.noisevisionproductions.playmeet.Chat;

import android.os.Build;

import com.noisevisionproductions.playmeet.UserManagement.UserModel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChatMessageModel extends RealmObject {
    // private String userId;
    @PrimaryKey
    private String uuid;
    private long timestamp;
    private RealmList<UserModel> userModels;
    private String message;

    public ChatMessageModel() {
// za każdym razem, gdy jest tworzona nowa wiadomość, zostaje generowane unikalne id dla wiadomości
        this.uuid = UUID.randomUUID().toString();
    }

    public ChatMessageModel(String uuid, RealmList<UserModel> userModels, String message, long timestamp) {
        this.uuid = uuid;
        this.userModels = userModels;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public RealmList<UserModel> getUsers() {
        return userModels;
    }

    public void setUsers(RealmList<UserModel> userModels) {
        this.userModels = userModels;
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
// formatuje date oraz godzine, aby była bardziej czytelna w każdej wiadomości
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime timestampAsDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        return timestampAsDateTime.format(dateTimeFormatter);
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
