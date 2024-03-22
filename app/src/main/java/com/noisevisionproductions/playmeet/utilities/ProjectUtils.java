package com.noisevisionproductions.playmeet.utilities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.noisevisionproductions.playmeet.ActivityMainMenu;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.dataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.firstSetup.ContainerForDialogFragment;
import com.noisevisionproductions.playmeet.loginRegister.LoginAndRegisterActivity;

import java.util.ArrayList;
import java.util.List;

public class ProjectUtils extends AppCompatActivity {
    public static void backToMainMenuButton(@NonNull AppCompatButton button, @NonNull final Context context) {
        button.setOnClickListener(view -> {
            Intent intent = new Intent(context, ActivityMainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        });
    }

    public static void hideSoftKeyboard(@NonNull Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            currentFocus.clearFocus();
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
        Snackbar snackBarLogin = Snackbar.make(view, context.getString(R.string.onlyForLoggedInUsers), Snackbar.LENGTH_SHORT)
                .setTextColor(Color.WHITE)
                .setActionTextColor(Color.GREEN);
        snackBarLogin.setAction(context.getString(R.string.loginButton), v -> {
            Intent intent = new Intent(context, LoginAndRegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        });
        snackBarLogin.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).setDuration(700);
        snackBarLogin.show();
    }

    public static void createSnackBarUsingViewVeryShort(@NonNull View view, @NonNull String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setDuration(600).show();
    }

    public static void createSnackBarOnTop(Activity activity, String message, int textColor) {
        View rootView = activity.findViewById(android.R.id.content);

        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        snackBarView.setLayoutParams(params);

        TextView textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(textColor);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        snackbar.show();
    }

    public static boolean isCityChosenFromTheList(String cityName, Context context) {
        List<String> cityList = new ArrayList<>(CityXmlParser.parseCityNames(context));
        return cityList.contains(cityName);
    }

    public static void copyTextOnClick(Context context, String label, String copiedText) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(label, copiedText);
        clipboardManager.setPrimaryClip(clipData);
    }
}
