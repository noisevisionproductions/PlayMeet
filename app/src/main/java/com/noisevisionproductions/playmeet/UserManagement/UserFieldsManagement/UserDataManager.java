package com.noisevisionproductions.playmeet.UserManagement.UserFieldsManagement;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;

import java.util.HashMap;

public class UserDataManager {
    public static void saveUserData(FirebaseUser firebaseUser, String label, String newValue, View view, Context context) {
        // usuwam ostatni znak z label, bo to ":"
        String newLabel = label.substring(0, label.length() - 1).toLowerCase();
        if (firebaseUser != null) {
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.getData(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // pobieram dane użytkownika z bazy
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            // w metodzie handleFieldUpdate zapisuje dane użytkownika w HashMap w celu wywołania
                            // metody updateUser z FirebaseHelper, która automatycznie zapisuje podane jej dane
                            // oraz wyświetla komunikaty o sukcesie oraz błędach
                            HashMap<String, Object> update = handleFieldUpdate(userModel, label, newValue, context);
                            firebaseHelper.updateDataUsingHashMap(update, aVoid -> Snackbar.make(view, "Zapisano " + newLabel, Snackbar.LENGTH_SHORT).setDuration(400).show(),
                                    error -> Log.e("Firebase Update", "Wystąpił błąd podczas aktualizacji danych.", error), "UserModel");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            }, "UserModel");
        }
    }

    private static HashMap<String, Object> handleFieldUpdate(UserModel userModel, String label, String
            newValue, Context context) {
        String provideName = context.getString(R.string.provideName);
        String provideCity = context.getString(R.string.provideCity);
        String providedAge = context.getString(R.string.provideAge);
        String providedAboutMe = context.getString(R.string.provideAboutYou);
        HashMap<String, Object> update = new HashMap<>();

        // zalezy, które pole jest edytowane, to pobiera wybrane dane tylko
        // z pola, które jest mu przypisane
        if (label.equals(provideName)) {
            if (!newValue.isEmpty() && !newValue.equals(provideName)) {
                userModel.setName(newValue);
                // zapisuje podane przez użytkownika dane w HashMap
                update.put("name", newValue);
            }
        } else if (label.equals(provideCity)) {
            if (!newValue.equals(provideCity)) {
                userModel.setLocation(newValue);
                update.put("location", newValue);
            }
        } else if (label.equals(providedAge)) {
            if (!newValue.equals(providedAge)) {
                userModel.setAge(newValue);
                update.put("age", newValue);
            }
        } else if (label.equals(providedAboutMe)) {
            if (!newValue.equals(providedAboutMe)) {
                userModel.setAboutMe(newValue);
                update.put("aboutMe", newValue);
            }
        }
        return update;
    }
}

