package com.example.zagrajmy.PostsManagement.PostsFiltering;

import com.example.zagrajmy.PostCreating;

public class PostIDFilter extends Filter {
    private final int postId;

    public PostIDFilter(boolean isEnabled, String postId) {
        super(isEnabled);
        this.postId = Integer.parseInt(postId);
    }

    @Override
    public boolean apply(PostCreating post) {
        return post.getPostId().equals(postId);
    }
}
