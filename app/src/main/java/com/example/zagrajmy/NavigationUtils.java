package com.example.zagrajmy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zagrajmy.PostsManagement.MainMenuPosts;

import java.util.Objects;

public class NavigationUtils extends AppCompatActivity {
    public static void backToMainMenuButton(Button button, final Context context) {
        button.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainMenuPosts.class);
            context.startActivity(intent);
        });
    }

    public static void hideKeyboardForFragments(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardAfterSendingMsg(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = ((Activity) context).getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

}
