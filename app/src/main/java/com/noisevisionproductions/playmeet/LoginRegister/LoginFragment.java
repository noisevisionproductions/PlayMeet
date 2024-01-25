package com.noisevisionproductions.playmeet.LoginRegister;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.Firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;
import com.noisevisionproductions.playmeet.Utilities.ProjectUtils;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private FirebaseAuthManager authManager;
    private String emailString, passwordString;
    private AppCompatAutoCompleteTextView emailInput, passwordInput;
    private AppCompatButton loginButton;
    private SignInButton googleSignIn;
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
            , result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthGoogle(account.getIdToken());
                    } catch (Exception e) {
                        Snackbar.make(requireView(), "Błąd logowania" + e.getMessage(), Snackbar.LENGTH_SHORT)
                                .setTextColor(Color.RED).show();
                        Log.e("google login", Objects.requireNonNull(e.getMessage()));
                    }
                }
            });
    private FirebaseAuth firebaseAuth;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        getUIObjects(view);
        guestButton(view);

        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
            Intent intent = new Intent(getContext(), MainMenuPosts.class);
            startActivity(intent);
        } else {
            loginButton.setOnClickListener(this::loginUser);
            googleSignIn.setOnClickListener(v -> logInWithGoogle());
        }

        LinearLayoutCompat mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(requireActivity()));


        return view;
    }

    private void logInWithGoogle() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions);

        Intent intent = googleSignInClient.getSignInIntent();
        launcher.launch(intent);
    }

    private void firebaseAuthGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            saveUserIdInDatabase(userId);

                            Toast.makeText(getActivity(), "Pomyślnie zalogowano", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainMenuPosts.class);
                            startActivity(intent);
                        }

                    } else {
                        Snackbar.make(requireView(), "Błąd logowania" + Objects.requireNonNull(task.getException()).getMessage(), Snackbar.LENGTH_SHORT)
                                .setTextColor(Color.RED).show();
                    }
                });
    }

    private void saveUserIdInDatabase(String userId) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel");
        userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    UserModel userModel = new UserModel();
                    userModel.setUserId(userId);
                    userReference.child(userModel.getUserId()).setValue(userModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Błąd podczas zapisu ID. Skontaktuj się z developerem", Toast.LENGTH_SHORT).show();
                Log.e("Firebase save userId", "Saving userID to database " + error.getMessage());
            }
        });
    }

    private void getUIObjects(View view) {
        authManager = new FirebaseAuthManager();

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
        googleSignIn = view.findViewById(R.id.googleSignIn);
    }

    private void loginUser(View view) {
        emailString = String.valueOf(emailInput.getText());
        passwordString = String.valueOf(passwordInput.getText());

        if (!checkValidation()) {
            authManager.userLogin(emailString, passwordString, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null) {
                        Toast.makeText(getActivity(), "Pomyślnie zalogowano", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainMenuPosts.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Użytkownik nie istnieje",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(getActivity(), "Błąd uwierzytelnienia: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
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

    private void guestButton(View view) {
        AppCompatTextView guestButton = view.findViewById(R.id.continueAsGuest);
        guestButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainMenuPosts.class);
            startActivity(intent);
        });
    }
}
