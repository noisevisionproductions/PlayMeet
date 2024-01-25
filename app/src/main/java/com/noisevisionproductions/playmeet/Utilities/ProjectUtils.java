package com.noisevisionproductions.playmeet.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.noisevisionproductions.playmeet.FirstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.LoginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;

public class ProjectUtils extends AppCompatActivity {
    public static void backToMainMenuButton(AppCompatButton button, final Context context) {
        button.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainMenuPosts.class);
            context.startActivity(intent);
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public static void handleCancelButtonForFragments(Button button, Fragment fragment) {
        button.setOnClickListener(v -> {
            if (fragment instanceof ContainerForDialogFragment) {
                ((ContainerForDialogFragment) fragment).dismiss();
            }
        });
    }

    public static void showLoginSnackBar(Context context) {
        View view = ((Activity) context).findViewById(android.R.id.content);
        Snackbar snackBarLogin = Snackbar.make(view, "Tylko dla zalogowanych użytkowników", Snackbar.LENGTH_SHORT)
                .setTextColor(Color.WHITE)
                .setActionTextColor(Color.GREEN);
        snackBarLogin.setAction("Zaloguj się", v -> {
            Intent intent = new Intent(context, LoginAndRegisterActivity.class);
            view.getContext().startActivity(intent);
        });
        snackBarLogin.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).setDuration(700);
        snackBarLogin.show();
    }

    public static void createSnackBarUsingViewVeryShort(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setDuration(600).show();
    }
}
