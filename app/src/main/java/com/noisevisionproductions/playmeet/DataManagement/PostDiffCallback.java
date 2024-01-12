package com.noisevisionproductions.playmeet.DataManagement;

import androidx.recyclerview.widget.DiffUtil;

import com.noisevisionproductions.playmeet.PostCreating;

import java.util.List;

public class PostDiffCallback extends DiffUtil.Callback {

    private final List<PostCreating> oldList;
    private final List<PostCreating> newList;

    public PostDiffCallback(List<PostCreating> oldList, List<PostCreating> newList) {
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
        return oldList.get(oldItemPosition).getPostId().equals(newList.get(newItemPosition).getPostId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Zastąp to swoją logiką porównywania zawartości
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
