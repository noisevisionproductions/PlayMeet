package com.noisevisionproductions.playmeet.appOptions;

import android.widget.CheckBox;

public class CheckBoxHandler {
    public interface OnCheckedChangeListener {
        void onCheckedChanged(boolean isChecked);
    }

    private OnCheckedChangeListener listener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public void handleCheckBox(CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onCheckedChanged(isChecked);
            }
        });
    }
}
