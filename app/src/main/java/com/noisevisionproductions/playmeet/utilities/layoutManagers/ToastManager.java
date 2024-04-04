package com.noisevisionproductions.playmeet.utilities.layoutManagers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.noisevisionproductions.playmeet.R;

public class ToastManager {
    private static Toast currentToast;

    public static void showToast(@NonNull Context context, String message) {
        if (currentToast != null) {
            // jeżeli toast już się wyświetla, to pojawia się na nowo, dzięki temu nie tworzy się jeden nad drugim
            currentToast.cancel();
        }

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View layout = layoutInflater.inflate(R.layout.toast_custom_layout, null);

        AppCompatTextView appCompatTextView = layout.findViewById(R.id.toastTextView);
        appCompatTextView.setText(message);

        currentToast = new Toast(context);
        currentToast.setDuration(Toast.LENGTH_SHORT);
        currentToast.setView(layout);
        currentToast.show();
    }

    public static void createToolTip(String message, AppCompatImageView infoIcon) {
        infoIcon.setTooltipText(message);
        infoIcon.setOnClickListener(View::performLongClick);
    }
}
