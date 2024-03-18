package com.noisevisionproductions.playmeet.firebase.interfaces;

public interface OnTokenFound {
    void onTokenFound(String token);

    void onFailure(Exception e);
}
