package com.noisevisionproductions.playmeet.PostsManagement.PostsFiltering;

import com.noisevisionproductions.playmeet.PostCreating;

public class DifficultyFilter extends Filter {
    private final String difficulty;

    public DifficultyFilter(boolean isEnabled, String difficulty) {
        super(isEnabled);
        this.difficulty = difficulty;
    }

    @Override
    public boolean apply(PostCreating post) {
        if (post == null || post.getSkillLevel() == null) {
            return false;
        }
        return post.getSkillLevel().equals(difficulty);
    }
}
