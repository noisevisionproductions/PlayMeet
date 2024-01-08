package com.example.zagrajmy.Chat;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PrivateChatModel extends RealmObject {
    @PrimaryKey
    private String roomId;
    private String nickNameOfOwnerOfThePost;
    private String nickNameOfUser2;
    private String userIdThatCreatedPost;
    private String user2;
    private RealmList<ChatMessageModel> messages;


    public PrivateChatModel() {
        this.roomId = UUID.randomUUID().toString();
    }

    public PrivateChatModel(String roomId, String userIdThatCreatedPost, String user2, RealmList<ChatMessageModel> messages) {
        this.roomId = (roomId != null) ? roomId : UUID.randomUUID().toString(); // generuje nowy roomId, gdy aktualny juz istnieje
        this.userIdThatCreatedPost = userIdThatCreatedPost;
        this.user2 = user2;
        this.messages = messages;
    }

    public String getNickNameOfOwnerOfThePost() {
        return nickNameOfOwnerOfThePost;
    }

    public void setNickNameOfOwnerOfThePost(String nickNameOfOwnerOfThePost) {
        this.nickNameOfOwnerOfThePost = nickNameOfOwnerOfThePost;
    }

    public String getNickNameOfUser2() {
        return nickNameOfUser2;
    }

    public void setNickNameOfUser2(String nickNameOfUser2) {
        this.nickNameOfUser2 = nickNameOfUser2;
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

    public void setLastMessage(ChatMessageModel lastMessage) {
        if (messages == null) {
            messages = new RealmList<>();
        }
        if (!messages.isEmpty()) {
            messages.remove(messages.size() - 1);
        }
        messages.add(lastMessage);

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
