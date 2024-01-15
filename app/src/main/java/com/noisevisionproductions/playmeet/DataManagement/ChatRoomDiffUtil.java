package com.noisevisionproductions.playmeet.DataManagement;

import androidx.recyclerview.widget.DiffUtil;

import com.noisevisionproductions.playmeet.Chat.ChatRoomModel;

import java.util.List;

public class ChatRoomDiffUtil extends DiffUtil.Callback {
    private final List<ChatRoomModel> oldList;
    private final List<ChatRoomModel> newList;

    public ChatRoomDiffUtil(List<ChatRoomModel> oldList, List<ChatRoomModel> newList) {
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
