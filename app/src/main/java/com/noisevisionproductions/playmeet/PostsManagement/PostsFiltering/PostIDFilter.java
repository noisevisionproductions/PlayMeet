package com.noisevisionproductions.playmeet.PostsManagement.PostsFiltering;

import com.noisevisionproductions.playmeet.PostCreating;

public class PostIDFilter extends Filter {
    private final String postId;

    public PostIDFilter(boolean isEnabled, String postId) {
        super(isEnabled);

        // sprawdzam czy postId nie jest pusty
        this.postId = postId;
    }

    @Override
    public boolean apply(PostCreating post) {
        // nie filtruje postów, jesli postId nie jest ustawione
        if (post == null || post.getPostId() == null) {
            return false;
        }
        return post.getPostId().contains(postId);
    }
}
