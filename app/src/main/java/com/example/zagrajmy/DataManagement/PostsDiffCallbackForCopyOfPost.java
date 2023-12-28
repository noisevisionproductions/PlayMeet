package com.example.zagrajmy.DataManagement;

import androidx.recyclerview.widget.DiffUtil;

import com.example.zagrajmy.PostCreatingCopy;

import java.util.List;

public class PostsDiffCallbackForCopyOfPost extends DiffUtil.Callback {

    private final List<PostCreatingCopy> oldList;
    private final List<PostCreatingCopy> newList;

    public PostsDiffCallbackForCopyOfPost(List<PostCreatingCopy> oldList, List<PostCreatingCopy> newList) {
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
        return oldList.get(oldItemPosition).getPostUuid().equals(newList.get(newItemPosition).getPostUuid());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Zastąp to swoją logiką porównywania zawartości
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

}