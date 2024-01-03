package com.example.zagrajmy.Realm;

import android.app.Application;

import com.example.zagrajmy.BuildConfig;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class RealmAppConfig extends Application {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        String realmId = BuildConfig.RealmAppId;
        app = new App(new AppConfiguration.Builder(realmId).build());
    }

    public static App getApp() {
        return app;
    }
}
