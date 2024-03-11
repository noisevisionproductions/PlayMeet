package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import androidx.annotation.Nullable;

import com.noisevisionproductions.playmeet.PostModel;

public class PostIDFilter extends Filter {
    private final String postId;

    public PostIDFilter(boolean isEnabled, String postId) {
        super(isEnabled);

        // sprawdzam czy postId nie jest pusty
        this.postId = postId;
    }

    @Override
    public boolean apply(@Nullable PostModel post) {
        // nie filtruje postów, jesli postId nie jest ustawione
        if (post == null || post.getPostId() == null) {
            return false;
        }
        return post.getPostId().contains(postId);
    }
}
