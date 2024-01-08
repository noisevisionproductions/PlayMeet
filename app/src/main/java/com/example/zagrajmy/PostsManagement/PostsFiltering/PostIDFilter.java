package com.example.zagrajmy.PostsManagement.PostsFiltering;

import com.example.zagrajmy.PostCreating;

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
        if (postId == -1) {
            return true;
        }
        // jesli nie jest pusty, to filtruje posty na podstawie postId
        return post.getPostId().equals(postId);
    }
}
