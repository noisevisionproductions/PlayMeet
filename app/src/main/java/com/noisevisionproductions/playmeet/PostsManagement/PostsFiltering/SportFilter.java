package com.noisevisionproductions.playmeet.PostsManagement.PostsFiltering;

import com.noisevisionproductions.playmeet.PostCreating;

public class SportFilter extends Filter {
    private final String sport;

    public SportFilter(boolean isEnabled, String sport) {
        super(isEnabled);
        this.sport = sport;
    }

    @Override
    public boolean apply(PostCreating post) {
        if (post == null || post.getSportType() == null) {
            return false;
        }
        return post.getSportType().equals(sport);
    }
}
