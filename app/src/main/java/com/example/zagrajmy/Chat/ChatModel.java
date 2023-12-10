package com.example.zagrajmy.Chat;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ChatModel extends RealmObject {
    private String chatId;
    private RealmList<ChatMessageModel> messages;


    public ChatModel() {
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public RealmList<ChatMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(RealmList<ChatMessageModel> messages) {
        this.messages = messages;
    }
}
