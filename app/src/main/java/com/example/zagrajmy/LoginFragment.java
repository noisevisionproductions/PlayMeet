package com.example.zagrajmy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {

    private Button loginButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Tutaj możesz utworzyć widok fragmentu
        View view = inflater.inflate(R.layout.activity_login, container, false);

        loginButton = view.findViewById(R.id.loginButton);



        return view;
    }
}
