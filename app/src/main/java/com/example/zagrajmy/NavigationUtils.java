package com.example.zagrajmy;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

public class NavigationUtils {
    public static void backToMainMenuButton(Button button, final Context context) {
        button.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainMenu.class);
            context.startActivity(intent);
        });
    }
}
