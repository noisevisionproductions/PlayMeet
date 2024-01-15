package com.noisevisionproductions.playmeet.PostsManagement.PostsFiltering;

import com.noisevisionproductions.playmeet.PostCreating;

public class PostIDFilter extends Filter {
    private final int postId;

    public PostIDFilter(boolean isEnabled, String postId) {
        super(isEnabled);

        // sprawdzam czy postId nie jest pusty
        this.postId = postId.isEmpty() ? -1 : Integer.parseInt(postId);
    }

    @Override
    public boolean apply(PostCreating post) {
        // nie filtruje postów, jesli postId nie jest ustawione
        return postId == -1;
        // jesli nie jest pusty, to filtruje posty na podstawie postId
    }
}
