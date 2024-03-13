package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;


import com.google.firebase.firestore.Query;

public class CityFilter extends Filter {
    private final String city;

    public CityFilter(boolean isEnabled, String city) {
        super(isEnabled);
        this.city = city;
    }

    @Override
    public Query applyFilter(Query baseQuery) {
        if (!isEnabled) {
            return baseQuery;
        }
        return baseQuery.whereEqualTo("cityName", city);
    }
}