package com.noisevisionproductions.playmeet.design.aboutApp;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.widget.AppCompatButton;

import com.noisevisionproductions.playmeet.AppOptions;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.design.TopMenuLayout;

public class ActivityToS extends TopMenuLayout {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tos);

        AppCompatButton backToMainMenu = findViewById(R.id.backToMainMenu);
        backToMainMenu.setOnClickListener(v -> backToAppInfo());

        loadPrivacyPolicy();
    }

    private void loadPrivacyPolicy() {
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/tos.html");
    }

    private void backToAppInfo() {
        Intent intent = new Intent(getApplicationContext(), AppOptions.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}