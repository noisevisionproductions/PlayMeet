package com.example.zagrajmy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;

public class PostCreatingLogic extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creating);

        Button button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);

        setUniqueId();

    }

    public void setUniqueId() {
        Button createPost = findViewById(R.id.submitPost);
        Log.d("MyActivity", "testsetestsetsetes"); // Dodaj ten log

        createPost.setOnClickListener(view -> {
            Log.d("MyActivity", "Button clicked"); // Dodaj ten log

            PostCreating postCreating = new PostCreating();
            UniqueIdGenerator uniqueIdGenerator = new UniqueIdGenerator();

            int uniqueId = uniqueIdGenerator.generateUniqueId();
            postCreating.setUniqueId(uniqueId);

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealm(postCreating);
            realm.commitTransaction();

            Toast.makeText(PostCreatingLogic.this, "Post utworzony!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PostCreatingLogic.this, MainMenu.class);
            startActivity(intent);
        });


    }

}
