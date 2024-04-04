package com.noisevisionproductions.playmeet;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noisevisionproductions.playmeet.design.TopMenuLayout;
import com.noisevisionproductions.playmeet.design.aboutApp.ActivityPrivacyPolicy;
import com.noisevisionproductions.playmeet.design.aboutApp.ActivityToS;
import com.noisevisionproductions.playmeet.design.aboutApp.ActivityUELA;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.loginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.userManagement.DeleteUserFromDB;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;
import com.noisevisionproductions.playmeet.utilities.layoutManagers.ToastManager;

public class AppOptions extends TopMenuLayout {
    private AppCompatTextView versionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_options_menu);
        Log.e("LanguageTest", getString(R.string.filters));

        loadLayout();
        printVersionInfo();
    }

    private void loadLayout() {
        versionInfo = findViewById(R.id.versionInfo);

        AppCompatButton backToMainMenu = findViewById(R.id.backToMainMenu);
        if (!FirebaseAuthManager.isUserLoggedIn() || !FirebaseAuthManager.isUserLoggedInUsingGoogle()) {
            backToMainMenu.setVisibility(View.GONE);
        }
        ProjectUtils.backToMainMenuButton(backToMainMenu, AppOptions.this);

        AppCompatTextView privacyPolicy = findViewById(R.id.privacyPolicy);
        privacyPolicy.setPaintFlags(privacyPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        privacyPolicy.setOnClickListener(v -> switchToPrivacyPolicy());

        AppCompatTextView termsOfUse = findViewById(R.id.termsOfUse);
        termsOfUse.setPaintFlags(termsOfUse.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        termsOfUse.setOnClickListener(v -> switchToTermsOfUse());

        AppCompatTextView uela = findViewById(R.id.uela);
        uela.setPaintFlags(uela.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        uela.setOnClickListener(v -> switchToUELA());

        AppCompatTextView logoutAccount = findViewById(R.id.logoutAccount);
        logoutAccount.setOnClickListener(v -> logoutUser());

        if (isUserLoggedIn()) {
            logoutAccount.setVisibility(View.GONE);
        }

        deleteUser();
    }

    private void deleteUser() {
        AppCompatButton deleteAccount = findViewById(R.id.deleteAccount);
        if (isUserLoggedIn()) {
            deleteAccount.setVisibility(View.GONE);
        }
        DeleteUserFromDB deleteUserFromDB = new DeleteUserFromDB(this, getLayoutInflater());
        deleteAccount.setOnClickListener(v -> deleteUserFromDB.createDeleteConfirmationDialog());
    }

    private void printVersionInfo() {
        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            String text = getString(R.string.version, versionName);
            versionInfo.setText(text);

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Printing app version", "error while printing app version" + e.getMessage());
        }
    }

    private void logoutUser() {
        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                firebaseAuth.signOut();
                ToastManager.showToast(this, getString(R.string.logoutSuccessful));
                Intent intent = new Intent(this, LoginAndRegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private void switchToPrivacyPolicy() {
        Intent intent = new Intent(getApplicationContext(), ActivityPrivacyPolicy.class);
        startActivity(intent);
    }

    private void switchToTermsOfUse() {
        Intent intent = new Intent(getApplicationContext(), ActivityToS.class);
        startActivity(intent);
    }

    private void switchToUELA() {
        Intent intent = new Intent(getApplicationContext(), ActivityUELA.class);
        startActivity(intent);
    }

    private boolean isUserLoggedIn() {
        return !FirebaseAuthManager.isUserLoggedInUsingGoogle() && !FirebaseAuthManager.isUserLoggedIn();
    }
}
