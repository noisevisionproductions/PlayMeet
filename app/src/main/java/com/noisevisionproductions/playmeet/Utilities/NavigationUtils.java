package com.noisevisionproductions.playmeet.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.noisevisionproductions.playmeet.FirstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.google.android.material.snackbar.Snackbar;

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

    public static void handleCancelButtonForFragments(Button button, Fragment fragment) {
        button.setOnClickListener(v -> {
            if (fragment instanceof ContainerForDialogFragment) {
                ((ContainerForDialogFragment) fragment).dismiss();
            }
        });
    }

    public static void showOnlyForLoggedUserMessage(View view) {
        Snackbar snackbar = Snackbar.make(view, "Dostępne jedynie dla zalogowanych użytkowników!", Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackbarView.setLayoutParams(params);
        snackbar.show();
    }
}
