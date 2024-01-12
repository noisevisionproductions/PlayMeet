package com.noisevisionproductions.playmeet.Design;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.Firebase.RealmDataManager;
import com.noisevisionproductions.playmeet.LoginRegister.LoginAndRegisterActivity;
import com.google.firebase.FirebaseApp;

import io.realm.Realm;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmDataManager realm = RealmDataManager.getInstance();
       /* Realm.init(this);
        //realm.deleteMessagesAndChatRooms();
        realm.deleteAllRealmDataUseForTestingOnly();
        realm.realmMigrationResetDatabaseOnlyForTesting();
        realm.closeRealmDatabase();*/

        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_welcome_screen);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginAndRegisterActivity.class);

            startActivity(intent);
            finish();

        }, 500);
    }
}
