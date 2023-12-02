package com.example.zagrajmy.PostsManagement.PostsFiltering;

import com.example.zagrajmy.PostCreating;

public class DifficultyFilter extends Filter {
    private final String difficulty;

    public DifficultyFilter(boolean isEnabled, String difficulty) {
        super(isEnabled);
        this.difficulty = difficulty;
    }

    @Override
    public boolean apply(PostCreating post) {
        return post.getSkillLevel().equals(difficulty);
    }
}
