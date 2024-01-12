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
        return post.getCityName().equals(city);
    }
}