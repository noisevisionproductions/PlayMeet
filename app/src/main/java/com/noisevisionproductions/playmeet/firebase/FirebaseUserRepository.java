package com.noisevisionproductions.playmeet.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnJoinedPostsCountListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnResultListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnTokenFound;
import com.noisevisionproductions.playmeet.firebase.interfaces.UserRepository;
import com.noisevisionproductions.playmeet.userManagement.UserModel;

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
    public void getUser(String userId, OnResultListener listener) {
        userReference.child("UserModel").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    listener.onSuccess();
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
        getUser(userId, new OnResultListener() {
            @Override
            public void onSuccess() {
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
        userReference.child("UserModel")
                .child(userId)
                .removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void getUserToken(String userId, OnTokenFound listener) {
        userReference.child("UserModel").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                if (userModel != null) {
                    String otherUserToken = userModel.getFcmToken();
                    if (otherUserToken != null) {
                        listener.onTokenFound(otherUserToken);
                    } else {
                        listener.onFailure(new Exception("Brak tokenu"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError e) {
                Log.e("usermodel info for notification error", "usermodel info for notification error " + e.getMessage());
            }
        });
    }

    @Override
    public void getJoinedPostsCount(String userId, OnJoinedPostsCountListener listener) {
        userReference.child("UserModel")
                .child(userId)
                .child("joinedPostsCount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Integer count = snapshot.getValue(Integer.class);
                            if (count != null) {
                                listener.onCountReceived(count);
                            } else {
                                listener.onFailure(new Exception("Nie udało się odczytać liczby dołączonych postów."));
                            }
                        } else {
                            listener.onCountReceived(0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure(new Exception(error.toException()));
                    }
                });
    }

    @Override
    public void incrementJoinedPostsCount(String userId, OnCompletionListener listener) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(userId);
        userReference.child("joinedPostsCount").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer count = currentData.getValue(Integer.class);
                if (count == null) {
                    currentData.setValue(1);
                } else if (count < 3) {
                    currentData.setValue(count + 1);
                } else {
                    return Transaction.abort();
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    listener.onFailure(error.toException());
                } else if (!committed) {
                    listener.onFailure(new Exception("Osiągnięto limit dołączonych postów."));
                } else {
                    listener.onSuccess();
                }
            }
        });
    }

    public void decrementJoinedPostsCount(String userId, OnCompletionListener listener) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(userId);
        userReference.child("joinedPostsCount").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer count = currentData.getValue(Integer.class);
                if (count == null) {
                    currentData.setValue(1);
                } else if (count > 0) {
                    currentData.setValue(count - 1);
                } else {
                    return Transaction.abort();
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    listener.onFailure(error.toException());
                } else if (!committed) {
                    listener.onFailure(new Exception("Brak postów do odłączenia."));
                } else {
                    listener.onSuccess();
                }
            }
        });
    }
}
