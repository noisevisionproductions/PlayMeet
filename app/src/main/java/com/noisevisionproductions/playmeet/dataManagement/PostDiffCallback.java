package com.noisevisionproductions.playmeet.dataManagement;

import androidx.recyclerview.widget.DiffUtil;

import com.noisevisionproductions.playmeet.PostModel;

import java.util.List;

public class PostDiffCallback extends DiffUtil.Callback {
    private final List<PostModel> oldList;
    private final List<PostModel> newList;

    public PostDiffCallback(List<PostModel> oldList, List<PostModel> newList) {
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
        return oldList.get(oldItemPosition).getPostId().equals(newList.get(newItemPosition).getPostId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        PostModel oldItem = oldList.get(oldItemPosition);
        PostModel newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem); // Upewnij się, że klasa PostModel implementuje metodę equals()
    }
}
