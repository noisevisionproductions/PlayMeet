package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import com.google.firebase.firestore.Query;

public class PostIDFilter extends Filter {
    private final String postId;

    public PostIDFilter(boolean isEnabled, String postId) {
        super(isEnabled);

        // sprawdzam czy postId nie jest pusty
        this.postId = postId;
    }

    @Override
    public Query applyFilter(Query baseQuery) {
        if (!isEnabled) {
            return baseQuery;
        }
        return baseQuery.whereEqualTo("postId", postId);
    }
}
