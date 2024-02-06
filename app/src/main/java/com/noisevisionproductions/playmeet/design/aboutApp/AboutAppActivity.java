package com.noisevisionproductions.playmeet.design.aboutApp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.noisevisionproductions.playmeet.design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

public class AboutAppActivity extends SidePanelBaseActivity {
    private AppCompatTextView versionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        // zaladowanie panelu bocznego
        setupDrawerLayout();
        setupNavigationView();

        loadLayout();
        printVersionInfo();
    }

    private void loadLayout() {
        AppCompatTextView privacyPolicy = findViewById(R.id.privacyPolicy);
        privacyPolicy.setPaintFlags(privacyPolicy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        AppCompatTextView termsOfUse = findViewById(R.id.termsOfUse);
        termsOfUse.setPaintFlags(termsOfUse.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        versionInfo = findViewById(R.id.versionInfo);
        AppCompatTextView uela = findViewById(R.id.uela);
        uela.setPaintFlags(uela.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        AppCompatButton backToMainMenu = findViewById(R.id.backToMainMenu);
        if (!FirebaseAuthManager.isUserLoggedIn() || !FirebaseAuthManager.isUserLoggedInUsingGoogle()) {
            backToMainMenu.setVisibility(View.GONE);
        }

        ProjectUtils.backToMainMenuButton(backToMainMenu, AboutAppActivity.this);
        privacyPolicy.setOnClickListener(v -> switchToPrivacyPolicy());
        termsOfUse.setOnClickListener(v -> switchToTermsOfUse());
        uela.setOnClickListener(v -> switchToUELA());
    }

    private void printVersionInfo() {
        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            String text = getString(R.string.version, versionName);
            versionInfo.setText(text);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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
}
