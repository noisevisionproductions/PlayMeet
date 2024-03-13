package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import com.google.firebase.firestore.Query;

public class SportFilter extends Filter {
    private final String sport;

    public SportFilter(boolean isEnabled, String sport) {
        super(isEnabled);
        this.sport = sport;
    }

    @Override
    public Query applyFilter(Query baseQuery) {
        if (!isEnabled) {
            return baseQuery;
        }
        return baseQuery.whereEqualTo("sportType", sport);
    }
}
