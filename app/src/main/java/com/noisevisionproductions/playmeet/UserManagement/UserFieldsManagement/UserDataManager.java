package com.noisevisionproductions.playmeet.UserManagement.UserFieldsManagement;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;
import com.google.android.material.snackbar.Snackbar;

import io.realm.Realm;
import io.realm.mongodb.User;

public class UserDataManager {
    public static void saveUserData(User user, String label, String newValue, View view, Context context) {

        if (user != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransactionAsync(realm1 -> {
                    UserModel userModel = realm1.where(UserModel.class)
                            .equalTo("userId", user.getId())
                            .findFirst();
                    if (userModel != null) {
                        handleFieldUpdate(userModel, label, newValue, view, context);
                    }
                }, () -> {
                }, error -> Log.e("Realm Transaction", "Wystąpił błąd podczas aktualizacji danych.", error));
            }
        }
    }

    private static void handleFieldUpdate(UserModel userModel, String label, String newValue, View view, Context context) {
        String provideName = context.getString(R.string.provideName);
        String provideCity = context.getString(R.string.provideCity);
        String providedAge = context.getString(R.string.provideAge);
        String providedAboutMe = context.getString(R.string.provideAboutYou);
        // zalezy, które pole jest edytowane, to pobiera wybrane dane tylko
        // z pola, które jest mu przypisane
        if (label.equals(provideName)) {
            if (!newValue.isEmpty() && !newValue.equals(provideName)) {
                userModel.setName(newValue);
                Snackbar.make(view, "Zapisano imię", Snackbar.LENGTH_SHORT).show();
            }
        } else if (label.equals(provideCity)) {
            if (!newValue.equals(provideCity)) {
                userModel.setLocation(newValue);
                Snackbar.make(view, "Zapisano miasto", Snackbar.LENGTH_SHORT).show();
            }
        } else if (label.equals(providedAge)) {
            if (!newValue.equals(providedAge)) {
                userModel.setBirthDay(newValue);
                Snackbar.make(view, "Zapisano wiek", Snackbar.LENGTH_SHORT).show();
            }
        } else if (label.equals(providedAboutMe)) {
            if (!newValue.equals(providedAboutMe)) {
                userModel.setAboutMe(newValue);
                Snackbar.make(view, "Zapisano o Tobie", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}

