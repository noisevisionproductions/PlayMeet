package com.noisevisionproductions.playmeet.loginRegister;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noisevisionproductions.playmeet.ActivityMainMenu;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private FirebaseAuthManager authManager;
    private String emailString, passwordString;
    private AppCompatAutoCompleteTextView emailInput, passwordInput;
    private AppCompatButton loginButton;
    private SignInButton googleSignIn;
    private ActivityResultLauncher<Intent> launcher;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        // tworze instancje, aby wywolac metode, ktora pozwolic na zalogowanie sie za pomoca google
        GoogleSignInHelper googleSignInHelper = new GoogleSignInHelper(this);
        launcher = googleSignInHelper.getActivityResultLauncher();

        getUIObjects(view);
        guestButton(view);

        verifyLogin();

        LinearLayoutCompat mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(requireActivity()));

        return view;
    }

    private void getUIObjects(@NonNull View view) {
        authManager = new FirebaseAuthManager();

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
        googleSignIn = view.findViewById(R.id.googleSignIn);

        AppCompatTextView passwordForgotten = view.findViewById(R.id.passwordForgotten);
        passwordForgotten.setOnClickListener(v -> passwordForgottenDialog());
    }

    private void verifyLogin() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            boolean emailVerified = currentUser.isEmailVerified();
            boolean loggedInWithGoogle = FirebaseAuthManager.isUserLoggedInUsingGoogle();

            if (loggedInWithGoogle || emailVerified) {
                navigateToMainMenu();
            }
        }
        loginButton.setOnClickListener(this::loginUser);
        googleSignIn.setOnClickListener(v -> logInWithGoogle());
    }

    private void logInWithGoogle() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions);

        Intent intent = googleSignInClient.getSignInIntent();
        launcher.launch(intent);
    }

    private void loginUser(View view) {
        emailString = String.valueOf(emailInput.getText());
        passwordString = String.valueOf(passwordInput.getText());

        if (!checkValidation()) {
            authManager.userLogin(emailString, passwordString, task -> {
                if (task.isSuccessful()) {
                    if (getActivity() != null) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            if (firebaseUser.isEmailVerified()) {
                                ToastManager.showToast(getActivity(), "Pomyślnie zalogowano");
                                navigateToMainMenu();
                            } else {
                                ToastManager.showToast(getActivity(), "Zweryfikuj swój adres e-mail przed zalogowaniem");
                            }
                        } else {
                            ToastManager.showToast(getActivity(), "Użytkownik nie istnieje");
                        }
                    }
                } else {
                    String errorMessage = Objects.requireNonNull(task.getException()).getMessage();

                    Toast.makeText(requireContext(), "Błąd uwierzytelnienia: " + errorMessage, Toast.LENGTH_SHORT).show();
                    if (errorMessage != null) {
                        Log.d("Login error", "Login error " + errorMessage);
                    }
                }
            });
        }
    }

    private boolean checkValidation() {
        boolean isError = false;

        if (TextUtils.isEmpty(emailString)) {
            emailInput.setError("Wprowadź e-mail");
            isError = true;
        }
        if (TextUtils.isEmpty(passwordString)) {
            passwordInput.setError("Wprowadź hasło");
            isError = true;
        }

        return isError;
    }

    private void navigateToMainMenu() {
        Intent intent = new Intent(getContext(), ActivityMainMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void guestButton(@NonNull View view) {
        AppCompatTextView guestButton = view.findViewById(R.id.continueAsGuest);
        guestButton.setOnClickListener(v -> navigateToMainMenu());
    }

    private void passwordForgottenDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Resetowanie hasła");

        final EditText emailInput = new EditText(getContext());
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setHint("Wprowadź e-mail");
        emailInput.setHeight(200);
        builder.setView(emailInput);

        builder.setPositiveButton("Resetuj hasło", (dialog, which) -> {
            String email = emailInput.getText().toString().trim();
            sendPasswordResetEmail(email);
        });
        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void sendPasswordResetEmail(@NonNull String email) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (!TextUtils.isEmpty(email)) {
            if (getContext() != null) {
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                ToastManager.showToast(getContext(), "Link resetujący został wysłany na Twój adres email");
                            } else {
                                ToastManager.showToast(getContext(), "Błąd podczas wysyłania linku");
                            }
                        });
            } else {
                ToastManager.showToast(getContext(), "E-mail jest wymagany");
            }
        }
    }
}
