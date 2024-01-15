package com.noisevisionproductions.playmeet.Chat;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatRoomModel {
    private String roomId;
    private String nickNameOfOwnerOfThePost;
    private String nickNameOfUser2;
    private String userIdThatCreatedPost;
    private String user2;
    private Map<String, ChatMessageModel> messages;


    public ChatRoomModel() {
        this.roomId = UUID.randomUUID().toString();
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
        if (messages == null || messages.isEmpty()) {
            return null;
        } else {
            List<ChatMessageModel> messagesList = new ArrayList<>(messages.values());
            messagesList.sort(Comparator.comparing(ChatMessageModel::getTimestamp));
            return messagesList.get(messagesList.size() - 1);
        }
    }

    public void setLastMessage(ChatMessageModel lastMessage) {
        if (messages == null) {
            messages = new HashMap<>();
        }
        messages.put(lastMessage.getUuid(), lastMessage);
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Map<String, ChatMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, ChatMessageModel> messages) {
        this.messages = messages;
    }

}
