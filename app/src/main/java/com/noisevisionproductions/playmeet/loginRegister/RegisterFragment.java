package com.noisevisionproductions.playmeet.loginRegister;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noisevisionproductions.playmeet.ActivityMainMenu;
import com.noisevisionproductions.playmeet.AppOptions;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseUserRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {
    private String firstPasswordString, secondPasswordString;
    private AppCompatAutoCompleteTextView emailInput, userPasswordFirstInput, userPasswordSecondInput;
    private AppCompatButton registerButton;
    private FirebaseAuthManager firebaseAuthManager;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        getUIObjects(view);

        // sprawdzam, czy użytkownik jest już zalogowany. Jeśli tak, to przekierowuję go do głównego menu aplikacji
        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || (FirebaseAuthManager.isUserLoggedIn() && Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified())) {
            Intent intent = new Intent(getContext(), ActivityMainMenu.class);
            startActivity(intent);
        } else {
            // jeżeli nie, to pozwalam mu na rejestrację nowego konta
            registerButton.setOnClickListener(viewRegister -> registerUser());
        }

        LinearLayoutCompat mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(requireActivity()));

        return view;
    }

    private void getUIObjects(@NonNull View view) {
        firebaseAuthManager = new FirebaseAuthManager();

        registerButton = view.findViewById(R.id.registerButton);
        emailInput = view.findViewById(R.id.emailInput);
        userPasswordFirstInput = view.findViewById(R.id.userPasswordInput);
        userPasswordSecondInput = view.findViewById(R.id.userPasswordSecondInput);

        AppCompatTextView acceptDocuments = view.findViewById(R.id.acceptDocuments);
        acceptDocuments.setPaintFlags(acceptDocuments.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        acceptDocuments.setOnClickListener(v -> loadDocuments());
    }

    private void registerUser() {
        String emailString = emailInput.getText().toString();
        firstPasswordString = userPasswordFirstInput.getText().toString();
        secondPasswordString = userPasswordSecondInput.getText().toString();

        // po sprawdzeniu, czy podane dane są prawidłowe, aplikacja rejestruje użytkownika pomyślnie
        if (checkValidation()) {
            firebaseAuthManager.userRegister(emailString, firstPasswordString, task -> {
                // użycie operatora trójargumentowego - rezultat = (warunek) ? wartoscGdyPrawda : wartoscGdyFalsz
                String error = task.getException() != null ? task.getException().getMessage() : getString(R.string.registerError);
                if (task.isSuccessful()) {
                    if (getActivity() != null) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            firebaseUser.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                if (verificationTask.isSuccessful()) {
                                    ToastManager.showToast(getActivity(), getString(R.string.registerSuccessfulEmailSent));
                                } else {
                                    String verificationError = verificationTask.getException() != null ? verificationTask.getException().getMessage() : getString(R.string.errorWhileSendingLink);
                                    ToastManager.showToast(getActivity(), getString(R.string.registerSuccessfulBut) + verificationError);
                                }
                                redirectToLoginScreen();
                            });
                            saveUserModelToDatabase(firebaseUser.getUid());
                        }
                    }
                } else {
                    ToastManager.showToast(requireActivity(), getString(R.string.registerError) + " " + error);
                    Log.e("Register new user", "New user register error " + error);
                }
            });

        }
    }

    private void saveUserModelToDatabase(String userId) {
        UserModel userModel = new UserModel();
        userModel.setUserId(userId);
        FirebaseUserRepository firebaseUserRepository = new FirebaseUserRepository();
        firebaseUserRepository.addUser(userModel, new OnCompletionListener() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) {
                    Log.e("Saving User To DB", "User saved in DB.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Saving User To DB", "Failure saving user in DB " + e.getMessage());
            }
        });
    }

    private void redirectToLoginScreen() {
        Intent intent = new Intent(getContext(), LoginAndRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean validateAndSetError(@NonNull EditText field, String errorMessage, @NonNull Predicate<String> validationFunction) {
        // pobieram wprowadzony tekst, który chcę sprawdzić pod kątem poprawności
        String fieldValue = String.valueOf(field.getText());
        // sprawdzenie, czy wersja androida jest równa lub większa od wersji Nougat
        if (!validationFunction.test(fieldValue)) {
            field.setError(errorMessage);
            return true;
        }
        return false;
    }

    private boolean checkValidation() {
        if (validateAndSetError(emailInput, getString(R.string.fieldCantBeEmpty), this::isFieldNotEmpty))
            return false;
        if (validateAndSetError(userPasswordFirstInput, getString(R.string.fieldCantBeEmpty), this::isFieldNotEmpty))
            return false;
        if (validateAndSetError(userPasswordSecondInput, getString(R.string.fieldCantBeEmpty), this::isFieldNotEmpty))
            return false;
        if (validateAndSetError(emailInput, getString(R.string.wrongFormat), this::isValidEmail))
            return false;
        if (validateAndSetError(userPasswordSecondInput, getString(R.string.passwordDontMatch), this::arePasswordsTheSame))
            return false;
        return !validateAndSetError(userPasswordFirstInput, getString(R.string.passwordRequirements), this::isValidPassword);
    }

    private boolean arePasswordsTheSame(String username) {
        // porównuje ze sobą wprowadzone hasła, aby się upewnić, że są one takie same
        return firstPasswordString.equals(secondPasswordString);
    }

    private boolean isValidPassword(@NonNull String password) {
        // hasło musi mieć więcej niż 7 znaków, aby zostało zaakceptowane
        if (password.length() < 8) {
            return false;
        }

        // aby hasło było poprawne, upewniam się, że ma małą literę oraz cyfrę
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");

        for (char character : password.toCharArray()) {
            if (Character.isLowerCase(character)) {
                hasLowercase = true;
            } else if (Character.isDigit(character)) {
                hasDigit = true;
            }
        }

        return hasLowercase && hasDigit;
    }

    private boolean isValidEmail(@Nullable String email) {
        // sprawdzam, czy pole z e-mail reprezentuje poprawny pattern adresu e-mail
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }

    private boolean isFieldNotEmpty(@NonNull String input) {
        // pola nie mogą być puste
        return !input.isEmpty();
    }

    private void loadDocuments() {
        Intent intent = new Intent(getContext(), AppOptions.class);
        startActivity(intent);
    }
}
