package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import androidx.annotation.Nullable;

import com.noisevisionproductions.playmeet.PostCreating;

public class DifficultyFilter extends Filter {
    private final String difficulty;

    public DifficultyFilter(boolean isEnabled, String difficulty) {
        super(isEnabled);
        this.difficulty = difficulty;
    }

    @Override
    public boolean apply(@Nullable PostCreating post) {
        if (post == null || post.getSkillLevel() == null) {
            return false;
        }
        return post.getSkillLevel().equals(difficulty);
    }
}
