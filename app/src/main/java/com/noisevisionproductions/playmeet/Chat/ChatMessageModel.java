package com.noisevisionproductions.playmeet.Chat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ChatMessageModel {
    private String uuid;
    private String nickname;
    private long timestamp;
    private String userId;
    private String message;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String uuid, String userId, String nickname, String message, long timestamp) {
        this.uuid = uuid;
        this.userId = userId;
        this.nickname = nickname;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userModels) {
        this.userId = userModels;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String formatDate() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime timestampAsDateTime = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return timestampAsDateTime.format(dateTimeFormatter);
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
