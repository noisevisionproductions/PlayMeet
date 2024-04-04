package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

public class FilterFactory {
    public static Filter createFilter(String filterType, boolean isEnabled, Object... parameters) {
        switch (filterType) {
            case "Sport":
                return new SportFilter(isEnabled, (String) parameters[0]);
            case "City":
                return new CityFilter(isEnabled, (String) parameters[0]);
            case "Difficulty":
                // sprawdzam, czy parametr jest typu Integer i odpowiednio rzutuje
                if (parameters[0] instanceof Integer) {
                    return new DifficultyFilter(isEnabled, (Integer) parameters[0]);
                } else {
                    throw new IllegalArgumentException("Difficulty filter requires an integer parameter.");
                }
            case "PostID":
                return new PostIDFilter(isEnabled, (String) parameters[0]);
            default:
                throw new IllegalArgumentException("Unknown filter type: " + filterType);
        }
    }
}
