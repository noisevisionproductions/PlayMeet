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

import com.example.zagrajmy.Realm.RealmDataManager;
import com.example.zagrajmy.PostsManagement.MainMenuPosts;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.Realm.RealmAuthenticationManager;
import com.example.zagrajmy.UserManagement.UserModel;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class RegisterFragment extends Fragment {
    private final RealmDataManager realmDataManager = RealmDataManager.getInstance();
    private String emailText, hasloPierwszeText, hasloDrugieText, nicknameText;
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
        //nicknameFromRegister = view.findViewById(R.id.nicknameFromRegister);
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

    public void saveNicknameToRealm() {
        try (Realm realm = Realm.getDefaultInstance()) {
            App app = RealmAppConfig.getApp();
            io.realm.mongodb.User user = app.currentUser();
            if (user != null) {
                String userId = user.getId();

                realm.executeTransactionAsync(realm1 -> {
                    UserModel userModelClassForNickname = realm1.where(UserModel.class)
                            .equalTo("userId", userId)
                            .findFirst();
                    if (userModelClassForNickname != null) {
                        userModelClassForNickname.setNickName(nicknameText);
                    }
                });
            }
        }
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

    public boolean isUserNameAvailable(String username) {
        Realm realm = Realm.getDefaultInstance();
        UserModel userModel = realm.where(UserModel.class).equalTo("nickName", username).findFirst();
        // realm.close();
        return userModel != null;
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
        App realmApp = RealmAppConfig.getApp();
        User user = realmApp.currentUser();

        /*UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
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
                });*/
    }
}
