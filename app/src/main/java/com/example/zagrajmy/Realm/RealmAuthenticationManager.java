package com.example.zagrajmy.Realm;

import com.example.zagrajmy.BuildConfig;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;

public class RealmAuthenticationManager {
    private final App realmApp;
    private static final String REALM_APP_ID = BuildConfig.RealmAppId;

    public RealmAuthenticationManager() {
        realmApp = new App(new AppConfiguration.Builder(REALM_APP_ID).build());
    }

    public void userLogin(String email, String password, App.Callback<User> callback) {
        realmApp.loginAsync(Credentials.emailPassword(email, password), callback);
    }

    public void userRegister(String email, String password, App.Callback<Void> callback) {
        realmApp.getEmailPassword().registerUserAsync(email, password, callback);
    }

    public boolean isUserLoggedIn() {
        return realmApp.currentUser() != null;
    }
}
