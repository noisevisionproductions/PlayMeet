package com.noisevisionproductions.playmeet.firebase.interfaces;

import com.google.firebase.firestore.DocumentReference;

public interface OnPostCreatedListener {
    void onSuccess(DocumentReference documentReference);

    void onFailure(Exception e);
}
