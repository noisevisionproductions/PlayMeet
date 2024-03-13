package com.noisevisionproductions.playmeet.userManagement;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NicknameValidation {
    public interface NicknameValidationCallback {
        void onNicknameValidationError(String error);

        void onNicknameValidationSuccess();

        void onNicknameAvailable();

        void onNicknameUnavailable(String error);
    }

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 30;
    private static final String REGEX_PATTERN = "^[a-zA-Z0-9]+$";

    public static boolean validateNickname(String nickname, NicknameValidationCallback callback) {
        if (nickname.isEmpty()) {
            callback.onNicknameValidationError("Pole nie może być puste");
            return false;
        } else if (nickname.length() < MIN_LENGTH || nickname.length() > MAX_LENGTH) {
            callback.onNicknameValidationError("Nazwa użytkownika powinna mieć od " + MIN_LENGTH + " do " + MAX_LENGTH + " znaków");
            return false;
        } else if (!nickname.matches(REGEX_PATTERN)) {
            callback.onNicknameValidationError("Nazwa użytkownika może zawierać tylko litery i cyfry");
            return false;
        } else {
            callback.onNicknameValidationSuccess();
            return true;
        }
    }

    public static void isNicknameAvailable(String nickname, NicknameValidationCallback callback) {
        DatabaseReference nicknameReference = FirebaseDatabase.getInstance().getReference().child("UserModel");
        Query query = nicknameReference.orderByChild("nickname").equalTo(nickname);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onNicknameUnavailable("Nazwa użytkownika jest zajęta");
                } else {
                    callback.onNicknameAvailable();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onNicknameValidationError("Firebase Realtime Database error: " + error.getMessage());
            }
        });
    }
}

