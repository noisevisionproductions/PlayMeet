package com.example.zagrajmy.PostsManagement.PostsFiltering;

import com.example.zagrajmy.PostCreating;

public abstract class Filter {
    private boolean isEnabled;

    public Filter(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public abstract boolean apply(PostCreating post);
}
