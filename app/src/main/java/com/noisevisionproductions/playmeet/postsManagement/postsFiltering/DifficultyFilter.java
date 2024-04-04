package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import com.google.firebase.firestore.Query;

public class DifficultyFilter extends Filter {
    private final int skillLevel;

    public DifficultyFilter(boolean isEnabled, int skillLevel) {
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
