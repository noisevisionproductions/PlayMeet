package com.noisevisionproductions.playmeet.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noisevisionproductions.playmeet.userManagement.OnCompletionListener;
import com.noisevisionproductions.playmeet.userManagement.OnResultListener;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.userManagement.UserRepository;

import java.util.Map;

public class FirebaseUserRepository implements UserRepository {
    private final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public void addUser(UserModel user, OnCompletionListener listener) {
        userReference.child("UserModel").child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void getUser(String userId, OnResultListener<UserModel> listener) {
        userReference.child("UserModel").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    listener.onSuccess(user);
                } else {
                    listener.onFailure(new Exception("Nie znaleziono użytkownika"));
                }
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    @Override
    public void updateUser(String userId, Map<String, Object> updates, OnCompletionListener listener) {
        // Najpierw pobieramy aktualne dane użytkownika
        getUser(userId, new OnResultListener<>() {
            @Override
            public void onSuccess(UserModel user) {
                // Następnie aktualizujemy dane w bazie
                userReference.child("UserModel").child(userId).updateChildren(updates)
                        .addOnSuccessListener(aVoid -> listener.onSuccess())
                        .addOnFailureListener(listener::onFailure);
            }

            @Override
            public void onFailure(Exception e) {
                // Jeśli nie uda się pobrać użytkownika, zwracamy błąd
                listener.onFailure(e);
            }
        });
    }

    @Override
    public void deleteUser(String userId, OnCompletionListener listener) {
        userReference.child("UserModel").child(userId).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void updateJoinedPostsCount(String userId, int count, OnCompletionListener listener) {
        userReference.child("UserModel").child(userId).child("joinedPostsCount").setValue(count)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }
}
