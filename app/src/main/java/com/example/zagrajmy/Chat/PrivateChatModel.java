package com.example.zagrajmy.Chat;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PrivateChatModel extends RealmObject {
    @PrimaryKey
    private String roomId;
    private String userIdThatCreatedPost;
    private String user2;
    private RealmList<ChatMessageModel> messages;


    public PrivateChatModel() {
    }

    public PrivateChatModel(String roomId, String userIdThatCreatedPost, String user2, RealmList<ChatMessageModel> messages) {
        this.roomId = roomId;
        this.userIdThatCreatedPost = userIdThatCreatedPost;
        this.user2 = user2;
        this.messages = messages;
    }

    public void setUserIdThatCreatedPost(String userIdThatCreatedPost) {
        this.userIdThatCreatedPost = userIdThatCreatedPost;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getUserIdThatCreatedPost() {
        return this.userIdThatCreatedPost;
    }

    public String getUser2() {
        return this.user2;
    }

    public ChatMessageModel getLastMessage() {
        if (messages.isEmpty()) {
            return null;
        } else {
            return messages.last();
        }
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public RealmList<ChatMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(RealmList<ChatMessageModel> messages) {
        this.messages = messages;
    }
}
