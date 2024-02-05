package com.noisevisionproductions.playmeet.Design.AboutApp;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.widget.AppCompatButton;

import com.noisevisionproductions.playmeet.Design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.R;

public class ActivityToS extends SidePanelBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tos);

        // zaladowanie panelu bocznego
        setupDrawerLayout();
        setupNavigationView();

        AppCompatButton backToMainMenu = findViewById(R.id.backToMainMenu);
        backToMainMenu.setOnClickListener(v -> backToAppInfo());

        loadPrivacyPolicy();
    }

    private void loadPrivacyPolicy() {
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/tos.html");
    }

    private void backToAppInfo() {
        Intent intent = new Intent(getApplicationContext(), AboutAppActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}