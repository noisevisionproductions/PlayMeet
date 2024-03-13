package com.noisevisionproductions.playmeet.firebase.interfaces;

import com.noisevisionproductions.playmeet.PostModel;

import java.util.List;

import javax.annotation.Nullable;

public interface OnPostsFetchedListener {
    void onPostsFetched(@Nullable List<PostModel> posts, @Nullable Exception e);
}
