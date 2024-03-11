package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import com.noisevisionproductions.playmeet.PostModel;

import java.util.List;

import javax.annotation.Nullable;

public interface OnPostsFetchedListener {
    void onPostsFetched(@Nullable List<PostModel> posts, @Nullable Exception e);
}
