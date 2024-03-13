package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import com.google.firebase.firestore.Query;

public class DifficultyFilter extends Filter {
    private final String skillLevel;

    public DifficultyFilter(boolean isEnabled, String skillLevel) {
        super(isEnabled);
        this.skillLevel = skillLevel;
    }

    @Override
    public Query applyFilter(Query baseQuery) {
        if (!isEnabled) {
            return baseQuery;
        }
        return baseQuery.whereEqualTo("skillLevel", skillLevel);
    }
}
