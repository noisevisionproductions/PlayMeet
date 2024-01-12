package com.noisevisionproductions.playmeet.Firebase;

import android.app.Application;


import io.realm.Realm;
import io.realm.mongodb.App;

public class RealmAppConfig extends Application {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

    public static App getApp() {
        return app;
    }
}
