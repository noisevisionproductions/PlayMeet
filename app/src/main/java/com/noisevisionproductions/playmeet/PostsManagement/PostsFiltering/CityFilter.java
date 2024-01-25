package com.noisevisionproductions.playmeet.PostsManagement.PostsFiltering;


import com.noisevisionproductions.playmeet.PostCreating;

public class CityFilter extends Filter {
    private final String city;

    public CityFilter(boolean isEnabled, String city) {
        super(isEnabled);
        this.city = city;
    }

    @Override
    public boolean apply(PostCreating post) {
        if (post == null || post.getCityName() == null) {
            return false;
        }
        return post.getCityName().equals(city);
    }
}