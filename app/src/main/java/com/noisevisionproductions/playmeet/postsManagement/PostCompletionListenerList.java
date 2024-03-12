package com.noisevisionproductions.playmeet.postsManagement;

import java.util.List;

public interface PostCompletionListenerList {
    void onSuccess(List<String> userIdsSignedUp);

    void onFailure(Exception e);
}
