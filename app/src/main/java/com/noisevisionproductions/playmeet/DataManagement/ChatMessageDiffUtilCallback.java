package com.noisevisionproductions.playmeet.DataManagement;

import androidx.recyclerview.widget.DiffUtil;


import com.noisevisionproductions.playmeet.Chat.ChatMessageModel;

import java.util.List;

// klasa potrzebna tak jak inne klasy typu DiffUtill w celu optymalizacji wyświetlania listy obiektów/danych w RecyclerView oraz do odświeżania danych, kiedy zostały zmienione
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
        return oldList.get(oldItemPosition).getUuid().equals(newList.get(newItemPosition).getUuid());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}