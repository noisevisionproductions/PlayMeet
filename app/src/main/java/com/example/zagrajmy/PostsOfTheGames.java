package com.example.zagrajmy;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PostsOfTheGames extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        Button button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
    }

}
