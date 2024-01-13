package com.noisevisionproductions.playmeet.Design;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.noisevisionproductions.playmeet.R;

// klasa abstrakcyjna w celu uniwersalnego tworzenia przycisku w wybranych fragmentach, który umożliwia stworzenie nowego posta
public abstract class BaseFragmentForAddPostButton extends Fragment {

    protected abstract int getLayoutId();

    protected abstract void onButtonClicked();

    protected abstract void showInfo();

    protected abstract void hideInfo();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(getLayoutId(), viewGroup, false);

        AppCompatButton addPostButton = view.findViewById(R.id.addPostButton);
        if (addPostButton != null) {
            addPostButton.setOnClickListener(v -> onButtonClicked());
            addPostButton.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showInfo();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    hideInfo();
                }
                return false;
            });
        }
        return view;
    }
}
