package com.noisevisionproductions.playmeet.firebase.interfaces;

public interface OnResultListener<T> {
    void onSuccess();

    void onFailure(Exception e);
}
