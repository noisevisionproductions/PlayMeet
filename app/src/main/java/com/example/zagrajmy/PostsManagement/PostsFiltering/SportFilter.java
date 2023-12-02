package com.example.zagrajmy.PostsManagement.PostsFiltering;

import com.example.zagrajmy.PostCreating;

public class SportFilter extends Filter {
    private final String sport;

    public SportFilter(boolean isEnabled, String sport) {
        super(isEnabled);
        this.sport = sport;
    }

    @Override
    public boolean apply(PostCreating post) {
        return post.getSportType().equals(sport);
    }

}
