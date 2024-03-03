package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import com.noisevisionproductions.playmeet.PostCreating;

public abstract class Filter {
    private final boolean isEnabled;

    public Filter(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public abstract boolean apply(PostCreating post);
}