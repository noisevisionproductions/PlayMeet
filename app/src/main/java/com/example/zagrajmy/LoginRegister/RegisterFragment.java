package com.example.zagrajmy.LoginRegister;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.MainMenu;
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    private String emailText, hasloPierwszeText, hasloDrugieText, nicknameText;
    private AppCompatAutoCompleteTextView email, nicknameFromRegister, hasloPierwsze, hasloDrugie;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register, container, false);
        User userClass = new User();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getContext(), MainMenu.class);
            startActivity(intent);
        }

        email = view.findViewById(R.id.email);
        nicknameFromRegister = view.findViewById(R.id.nicknameFromRegister);
        hasloPierwsze = view.findViewById(R.id.hasloPierwsze);
        hasloDrugie = view.findViewById(R.id.hasloDrugie);

        AppCompatButton przyciskRejestracji = view.findViewById(R.id.registerButton);

        przyciskRejestracji.setOnClickListener(view1 -> {

            nicknameText = nicknameFromRegister.getText().toString();
            emailText = email.getText().toString();
            hasloPierwszeText = hasloPierwsze.getText().toString();
            hasloDrugieText = hasloDrugie.getText().toString();

            userClass.setNickName(nicknameText);

            checkValidation();

            AuthenticationManager authManager = new AuthenticationManager();

            authManager.userRegister(emailText, hasloPierwszeText, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Konto założone", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), LoginAndRegisterActivity.class);
                    startActivity(intent);

                    /*dodawanie nicku do bazy danych realm*/
                    RealmDatabaseManagement realmDatabaseManagement = new RealmDatabaseManagement();
                    realmDatabaseManagement.createUser();
                    realmDatabaseManagement.closeRealmDatabase();

                    saveNicknameToFirebase();

                } else {
                    String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(getActivity(), "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

        });
        return view;
    }

    public boolean validateAndSetError(EditText field, String errorMessage, Predicate<String> validationFunction) {
        String fieldValue = String.valueOf(field.getText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!validationFunction.test(fieldValue)) {
                field.setError(errorMessage);
                return true;
            }
        }
        return false;
    }

    public void checkValidation() {
        if (validateAndSetError(email, "Pole nie może być puste", this::isFieldNotEmpty)) return;
        if (validateAndSetError(nicknameFromRegister, "Pole nie może być puste", this::isFieldNotEmpty))
            return;
        if (validateAndSetError(hasloPierwsze, "Pole nie może być puste", this::isFieldNotEmpty))
            return;
        if (validateAndSetError(hasloDrugie, "Pole nie może być puste", this::isFieldNotEmpty))
            return;
        if (validateAndSetError(nicknameFromRegister, "Nieprawidłowa nazwa użytkownika", this::isUsernameAlphanumeric))
            return;
        if (validateAndSetError(nicknameFromRegister, "Nazwa użytkownika jest za długa", this::isUsernameNotTooLong))
            return;
        if (validateAndSetError(nicknameFromRegister, "Nazwa użytkownika jest za krótka", this::isUsernameLongEnough))
            return;
        if (validateAndSetError(email, "Niepoprawny adres e-mail", this::isValidEmail)) return;
       /* if (validateAndSetError(hasloPierwsze, "Hasła nie pasują do siebie", this::arePasswordsTheSame))
            return;*/
       /* if (validateAndSetError(hasloPierwsze, "Hasło musi zawierać co najmniej jedną dużą literę, jedną małą literę, jedną cyfrę i jeden znak specjalny, oraz musi mieć co najmniej 8 znaków", this::isValidPassword))
            return;*/
    }

    public boolean arePasswordsTheSame(String username) {
        return hasloPierwszeText.equals(hasloDrugieText);
    }

    public boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = !password.matches("[a-zA-Z0-9 ]*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }


    public boolean isFieldNotEmpty(String username) {
        return !username.isEmpty();
    }

    public boolean isUsernameLongEnough(String username) {
        return username.length() >= 3;
    }

    public boolean isUsernameNotTooLong(String username) {
        return username.length() <= 30;
    }

    public boolean isUsernameAlphanumeric(String username) {
        return username.matches("[a-zA-Z0-9]*");
    }


    //Todo: set user nickname from UserProfileManager class

    public void saveNicknameToFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(String.valueOf(nicknameFromRegister.getText()))
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(taskk -> {
                    if (taskk.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                    } else {
                        Log.w(TAG, "User profile update failed.", taskk.getException());
                    }
                });
    }
}
