package com.noisevisionproductions.playmeet.design.aboutApp;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.widget.AppCompatButton;

import com.noisevisionproductions.playmeet.design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.R;

public class ActivityPrivacyPolicy extends SidePanelBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // zaladowanie panelu bocznego
        setupDrawerLayout();
        setupNavigationView();

        AppCompatButton backToMainMenu = findViewById(R.id.backToMainMenu);
        backToMainMenu.setOnClickListener(v -> backToAppInfo());

        loadPrivacyPolicy();
    }

    private void loadPrivacyPolicy() {
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/privacy_policy.html");
    }

    private void backToAppInfo() {
        Intent intent = new Intent(getApplicationContext(), AboutAppActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
