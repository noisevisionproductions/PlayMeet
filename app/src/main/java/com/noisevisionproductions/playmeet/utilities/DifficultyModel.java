package com.noisevisionproductions.playmeet.utilities;

import androidx.annotation.NonNull;

public record DifficultyModel(int id, String name) {

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
