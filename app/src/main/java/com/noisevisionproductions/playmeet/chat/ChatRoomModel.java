package com.noisevisionproductions.playmeet.chat;


import androidx.annotation.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomModel {
    private String roomId;
    private long timeStamp;
    private final Map<String, ChatMessageModel> messages = new HashMap<>();
    private final Map<String, Boolean> participants = new HashMap<>();


    public ChatRoomModel() {
    }

    public ChatRoomModel(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @NonNull
    public Map<String, Boolean> getParticipants() {
        return participants;
    }

    public String formatDate() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime timestampAsDateTime = Instant.ofEpochMilli(timeStamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return timestampAsDateTime.format(dateTimeFormatter);
    }
}
