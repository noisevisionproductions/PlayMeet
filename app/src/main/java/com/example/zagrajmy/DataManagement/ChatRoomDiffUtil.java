package com.example.zagrajmy.DataManagement;

import androidx.recyclerview.widget.DiffUtil;

import com.example.zagrajmy.Chat.PrivateChatModel;

import java.util.List;

public class ChatRoomDiffUtil extends DiffUtil.Callback {
    private final List<PrivateChatModel> oldList;
    private final List<PrivateChatModel> newList;

    public ChatRoomDiffUtil(List<PrivateChatModel> oldList, List<PrivateChatModel> newList) {
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
        return oldList.get(oldItemPosition).getRoomId().equals(newList.get(newItemPosition).getRoomId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
