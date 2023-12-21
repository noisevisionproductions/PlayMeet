package com.example.zagrajmy;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import com.example.zagrajmy.PostsManagement.MainMenuPosts;
import com.example.zagrajmy.PostsManagement.PostsOfTheGamesFragment;

public class NavigationUtils {
    public static void backToMainMenuButton(Button button, final Context context) {
        button.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainMenuPosts.class);
            context.startActivity(intent);
        });
    }
}
