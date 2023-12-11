package com.example.zagrajmy.DataManagement;

import androidx.recyclerview.widget.DiffUtil;

import com.example.zagrajmy.Chat.ChatMessageModel;

import java.util.List;

public class ChatMessageDiffUtilCallback extends DiffUtil.Callback {

    private final List<ChatMessageModel> oldList;
    private final List<ChatMessageModel> newList;


    public ChatMessageDiffUtilCallback(List<ChatMessageModel> oldList, List<ChatMessageModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Zastąp to swoją logiką porównywania identyfikatorów
        //return oldList.get(oldItemPosition).getPostId().equals(newList.get(newItemPosition).getPostId());
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Zastąp to swoją logiką porównywania zawartości
        //return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        return false;
    }
}