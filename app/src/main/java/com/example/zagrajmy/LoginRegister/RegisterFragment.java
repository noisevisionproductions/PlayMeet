package com.example.zagrajmy.LoginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.zagrajmy.LoginRegister.AuthenticationManager;
import com.example.zagrajmy.MainMenu;
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private String email, passwordFirst, passwordSecond;
    private TextInputEditText edytujPoleEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.activity_register, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getContext(), MainMenu.class);
            startActivity(intent);
        }

        edytujPoleEmail = currentView.findViewById(R.id.email);
        Button przyciskRejestracji = currentView.findViewById(R.id.registerButton);

        przyciskRejestracji.setOnClickListener(new View.OnClickListener() {
            final TextView hasloJeden = currentView.findViewById(R.id.hasloPierwsze);
            final TextView hasloDwa = currentView.findViewById(R.id.hasloDrugie);

            @Override
            public void onClick(View view) {
                email = String.valueOf(edytujPoleEmail.getText());
                passwordFirst = String.valueOf(hasloJeden.getText());
                passwordSecond = String.valueOf(hasloDwa.getText());

                if (emptyFieldsErrorHandle()) {
                    return;
                }

                AuthenticationManager authManager = new AuthenticationManager();

                authManager.userRegister(email, passwordFirst, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Konto założone",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainMenu.class);
                        startActivity(intent);
                    } else {
                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(getActivity(), "Authentication failed: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return currentView;
    }

    public boolean emptyFieldsErrorHandle() {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Wprowadź e-mail", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(passwordFirst) || TextUtils.isEmpty(passwordSecond)) {
            Toast.makeText(getActivity(), "Wprowadź hasło", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!passwordSecond.equals(passwordFirst)) {
            Toast.makeText(getActivity(), "Hasła nie pasują do siebie.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
