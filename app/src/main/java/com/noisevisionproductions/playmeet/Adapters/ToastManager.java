package com.noisevisionproductions.playmeet.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.noisevisionproductions.playmeet.R;

public class ToastManager {
    private static Toast currentToast;

    public static void showToast(Context context, String message) {
        if (currentToast != null) {
            // jeżeli toast już się wyświetla, to pojawia się na nowo, dzięki temu nie tworzy się jeden nad drugim
            currentToast.cancel();
        }

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View layout = layoutInflater.inflate(R.layout.toast_custom_layout, null);

        AppCompatTextView appCompatTextView = layout.findViewById(R.id.toastTextView);
        appCompatTextView.setTextColor(ContextCompat.getColor(context, R.color.accent));
        appCompatTextView.setText(message);

        currentToast = new Toast(context);
        currentToast.setDuration(Toast.LENGTH_SHORT);
        currentToast.setView(layout);
        currentToast.show();
    }
}
