package com.example.zagrajmy.PostsManagement.PostsFiltering;


import com.example.zagrajmy.PostCreating;

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