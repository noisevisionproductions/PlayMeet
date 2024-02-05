package com.noisevisionproductions.playmeet.LoginRegister;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noisevisionproductions.playmeet.Adapters.ToastManager;
import com.noisevisionproductions.playmeet.Design.AboutApp.AboutAppActivity;
import com.noisevisionproductions.playmeet.Firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;
import com.noisevisionproductions.playmeet.Utilities.ProjectUtils;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        getUIObjects(view);

        // sprawdzam, czy użytkownik jest już zalogowany. Jeśli tak, to przekierowuję go do głównego menu aplikacji
        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || (FirebaseAuthManager.isUserLoggedIn() && Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified())) {
            Intent intent = new Intent(getContext(), MainMenuPosts.class);
            startActivity(intent);
        } else {
            // jeżeli nie, to pozwalam mu na rejestrację nowego konta
            registerButton.setOnClickListener(viewRegister -> registerUser());
        }

        LinearLayout mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(requireActivity()));

        return view;
    }

    private void getUIObjects(View view) {
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
                String error = task.getException() != null ? task.getException().getMessage() : "Błąd rejestracji";
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null) {

                        firebaseUser.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                            if (verificationTask.isSuccessful()) {
                                ToastManager.showToast(getActivity(), "Rejestracja pomyślna. E-mail weryfikacyjny został wysłany!");
                            } else {
                                String verificationError = verificationTask.getException() != null ? verificationTask.getException().getMessage() : "Nie udało się wysłać e-mail'a weryfikacyjnego";
                                ToastManager.showToast(getActivity(), "Rejestracja pomyślna, ale " + verificationError);
                            }
                            redirectToLoginScreen();
                        });
                        saveUserModelToDatabase(firebaseUser.getUid());
                    }
                } else {
                    ToastManager.showToast(getActivity(), "Błąd rejestracji: " + error);
                    Log.e("Register new user", "New user register error " + error);
                }
            });

        }
    }

    private void saveUserModelToDatabase(String userId) {
        UserModel userModel = new UserModel();
        userModel.setUserId(userId);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("UserModel");
        usersRef.child(userModel.getUserId()).setValue(userModel);
    }

    private void redirectToLoginScreen() {
        Intent intent = new Intent(getContext(), LoginAndRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean validateAndSetError(EditText field, String errorMessage, Predicate<String> validationFunction) {
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
        if (validateAndSetError(emailInput, "Pole nie może być puste", this::isFieldNotEmpty))
            return false;
        if (validateAndSetError(userPasswordFirstInput, "Pole nie może być puste", this::isFieldNotEmpty))
            return false;
        if (validateAndSetError(userPasswordSecondInput, "Pole nie może być puste", this::isFieldNotEmpty))
            return false;
        if (validateAndSetError(emailInput, "Niepoprawny adres e-mail", this::isValidEmail))
            return false;
        if (validateAndSetError(userPasswordSecondInput, "Hasła nie pasują do siebie", this::arePasswordsTheSame))
            return false;
        return !validateAndSetError(userPasswordFirstInput, "Hasło musi zawierać co najmniej jedną dużą literę, jedną małą literę, jedną cyfrę i jeden znak specjalny, oraz musi mieć co najmniej 8 znaków", this::isValidPassword);
    }

    private boolean arePasswordsTheSame(String username) {
        // porównuje ze sobą wprowadzone hasła, aby się upewnić, że są one takie same
        return firstPasswordString.equals(secondPasswordString);
    }

    private boolean isValidPassword(String password) {
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

    private boolean isValidEmail(String email) {
        // sprawdzam, czy pole z e-mail reprezentuje poprawny pattern adresu e-mail
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }

    private boolean isFieldNotEmpty(String input) {
        // pola nie mogą być puste
        return !input.isEmpty();
    }

    private void loadDocuments() {
        Intent intent = new Intent(getContext(), AboutAppActivity.class);
        startActivity(intent);
    }
}
