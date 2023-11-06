package com.example.zagrajmy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class RegisterFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Tutaj możesz utworzyć widok fragmentu
        return inflater.inflate(R.layout.activity_register, container, false);
    }
}
