package com.noisevisionproductions.playmeet.LoginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;
import com.noisevisionproductions.playmeet.Utilities.NavigationUtils;

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
        if (firebaseAuthManager.isUserLoggedIn()) {
            Intent intent = new Intent(getContext(), MainMenuPosts.class);
            startActivity(intent);
        } else {
            // jeżeli nie, to pozwalam mu na rejestrację nowego konta
            registerButton.setOnClickListener(viewRegister -> registerUser());
        }

        LinearLayout mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> NavigationUtils.hideSoftKeyboard(requireActivity()));

        return view;
    }

    public void getUIObjects(View view) {
        firebaseAuthManager = new FirebaseAuthManager();

        registerButton = view.findViewById(R.id.registerButton);
        emailInput = view.findViewById(R.id.emailInput);
        userPasswordFirstInput = view.findViewById(R.id.userPasswordInput);
        userPasswordSecondInput = view.findViewById(R.id.userPasswordSecondInput);
    }

    public void registerUser() {
        String emailString = emailInput.getText().toString();
        firstPasswordString = userPasswordFirstInput.getText().toString();
        secondPasswordString = userPasswordSecondInput.getText().toString();

        // po sprawdzeniu, czy podane dane są prawidłowe, aplikacja rejestruje użytkownika pomyślnie
        if (checkValidation()) {
            firebaseAuthManager.userRegister(emailString, firstPasswordString, task -> {
                // użycie operatora trójargumentowego - rezultat = (warunek) ? wartoscGdyPrawda : wartoscGdyFalsz
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Błąd rejestracji";
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null) {

                        String userId = firebaseUser.getUid();

                        UserModel userModel = new UserModel();
                        userModel.setUserId(userId);
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("UserModel");
                        usersRef.child(userModel.getUserId()).setValue(userModel);

                        Toast.makeText(getActivity(), "Konto założone", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), LoginAndRegisterActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Błąd rejestracji: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public boolean validateAndSetError(EditText field, String errorMessage, Predicate<String> validationFunction) {
        // pobieram wprowadzony tekst, który chcę sprawdzić pod kątem poprawności
        String fieldValue = String.valueOf(field.getText());
        // sprawdzenie, czy wersja androida jest równa lub większa od wersji Nougat
        if (!validationFunction.test(fieldValue)) {
            field.setError(errorMessage);
            return true;
        }
        return false;
    }

    public boolean checkValidation() {
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

    public boolean arePasswordsTheSame(String username) {
        // porównuje ze sobą wprowadzone hasła, aby się upewnić, że są one takie same
        return firstPasswordString.equals(secondPasswordString);
    }

    public boolean isValidPassword(String password) {
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

    public boolean isValidEmail(String email) {
        // sprawdzam, czy pole z e-mail reprezentuje poprawny pattern adresu e-mail
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }

    public boolean isFieldNotEmpty(String input) {
        // pola nie mogą być puste
        return !input.isEmpty();
    }
}
