package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

public class FilterFactory {
    public static Filter createFilter(String filterType, boolean isEnabled, String... parameters) {
        return switch (filterType) {
            case "Sport" -> new SportFilter(isEnabled, parameters[0]);
            case "City" -> new CityFilter(isEnabled, parameters[0]);
            case "Difficulty" -> new DifficultyFilter(isEnabled, parameters[0]);
            case "PostID" -> new PostIDFilter(isEnabled, parameters[0]);
            default -> throw new IllegalArgumentException("Unknown filter type: " + filterType);
        };
    }
}
