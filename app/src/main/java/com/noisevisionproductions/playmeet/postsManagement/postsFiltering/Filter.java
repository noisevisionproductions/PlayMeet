package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import com.google.firebase.firestore.Query;

public abstract class Filter {
    protected boolean isEnabled;

    public Filter(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public abstract Query applyFilter(Query baseQuery);
}
