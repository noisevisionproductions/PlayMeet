package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import androidx.annotation.Nullable;

import com.noisevisionproductions.playmeet.PostModel;

public class SportFilter extends Filter {
    private final String sport;

    public SportFilter(boolean isEnabled, String sport) {
        super(isEnabled);
        this.sport = sport;
    }

    @Override
    public boolean apply(@Nullable PostModel post) {
        if (post == null || post.getSportType() == null) {
            return false;
        }
        return post.getSportType().equals(sport);
    }
}
