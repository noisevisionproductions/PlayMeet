package com.noisevisionproductions.playmeet.userManagement;

public interface OnResultListener<T> {
    void onSuccess(T result);

    void onFailure(Exception e);
}
