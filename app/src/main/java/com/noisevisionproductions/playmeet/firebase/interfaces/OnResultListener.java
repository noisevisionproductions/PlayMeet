package com.noisevisionproductions.playmeet.firebase.interfaces;

public interface OnResultListener<T> {
    void onSuccess(T result);

    void onFailure(Exception e);
}
