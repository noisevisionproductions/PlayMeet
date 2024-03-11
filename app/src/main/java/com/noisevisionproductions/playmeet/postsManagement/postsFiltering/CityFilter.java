package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;


import androidx.annotation.Nullable;

import com.noisevisionproductions.playmeet.PostModel;

public class CityFilter extends Filter {
    private final String city;

    public CityFilter(boolean isEnabled, String city) {
        super(isEnabled);
        this.city = city;
    }

    @Override
    public boolean apply(@Nullable PostModel post) {
        if (post == null || post.getCityName() == null) {
            return false;
        }
        return post.getCityName().equals(city);
    }
}