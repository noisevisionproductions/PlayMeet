package com.example.zagrajmy.LoginRegister;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.zagrajmy.PostsManagement.MainMenuPosts;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAuthenticationManager;
import com.example.zagrajmy.Realm.RealmDataManager;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {
    private final RealmDataManager realmDataManager = RealmDataManager.getInstance();
    private String emailText, hasloPierwszeText, hasloDrugieText;
    private AppCompatAutoCompleteTextView email, hasloPierwsze, hasloDrugie;
    private RealmAuthenticationManager realmAuthenticationManager;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        realmAuthenticationManager = new RealmAuthenticationManager();

        if (realmAuthenticationManager.isUserLoggedIn()) {
            Intent intent = new Intent(getContext(), MainMenuPosts.class);
            startActivity(intent);
        }

        email = view.findViewById(R.id.email);
        hasloPierwsze = view.findViewById(R.id.hasloPierwsze);
        hasloDrugie = view.findViewById(R.id.hasloDrugie);

        registerUserLogic(view);

        return view;
    }

    public void registerUserLogic(View view) {
        AppCompatButton registerUserButton = view.findViewById(R.id.registerButton);

        registerUserButton.setOnClickListener(view1 -> {

            checkValidation();

            emailText = email.getText().toString();
            hasloPierwszeText = hasloPierwsze.getText().toString();
            hasloDrugieText = hasloDrugie.getText().toString();

            realmAuthenticationManager.userRegister(emailText, hasloPierwszeText, task -> {
                if (task.isSuccess()) {
                    Toast.makeText(getActivity(), "Konto założone, możesz się zalogować", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), LoginAndRegisterActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Registration failed: " + task.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
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
        if (validateAndSetError(hasloPierwsze, "Pole nie może być puste", this::isFieldNotEmpty))
            return;
        if (validateAndSetError(hasloDrugie, "Pole nie może być puste", this::isFieldNotEmpty))
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
}
