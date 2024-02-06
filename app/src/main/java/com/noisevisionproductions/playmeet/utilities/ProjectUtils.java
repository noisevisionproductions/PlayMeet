package com.noisevisionproductions.playmeet.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.noisevisionproductions.playmeet.firstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.loginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.postsManagement.MainMenuPosts;

public class ProjectUtils extends AppCompatActivity {
    public static void backToMainMenuButton(@NonNull AppCompatButton button, @NonNull final Context context) {
        button.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainMenuPosts.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        });
    }

    public static void hideSoftKeyboard(@NonNull Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public static void handleCancelButtonForFragments(@NonNull Button button, Fragment fragment) {
        button.setOnClickListener(v -> {
            if (fragment instanceof ContainerForDialogFragment) {
                ((ContainerForDialogFragment) fragment).dismiss();
            }
        });
    }

    public static void showLoginSnackBar(@NonNull Context context) {
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

    public static void createSnackBarUsingViewVeryShort(@NonNull View view, @NonNull String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setDuration(600).show();
    }
}
