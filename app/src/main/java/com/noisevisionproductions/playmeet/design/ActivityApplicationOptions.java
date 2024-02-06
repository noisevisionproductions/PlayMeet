package com.noisevisionproductions.playmeet.design;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.noisevisionproductions.playmeet.adapters.ToastManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.loginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.postsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.userManagement.UserAccountLogic;

import java.nio.charset.StandardCharsets;

public class ActivityApplicationOptions extends SidePanelBaseActivity {

    @Nullable
    private FirebaseUser currentUser;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_menu);

        setupDrawerLayout();
        setupNavigationView();

        loadLayout();
    }

    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    private void loadLayout() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        AppCompatButton deleteAccount = findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(v -> createDeleteConfirmationDialog());

        AppCompatButton backToMainMenu = findViewById(R.id.backToMainMenu);
        backToMainMenu.setOnClickListener(v -> backToMainMenu());
    }

    private void backToMainMenu() {
        Intent intent = new Intent(getApplicationContext(), MainMenuPosts.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void createDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.dialog_get_password, null);

        setupDialogView(dialogView);

        builder.setTitle(getString(R.string.safetyPasswordCheck))
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        dialog = builder.create();
        setupDialogOnShow();

        if (!isFinishing()) {
            dialog.show();
        }
    }

    private void setupDialogView(@NonNull View dialogView) {
        TextInputLayout inputLayoutPassword = dialogView.findViewById(R.id.inputLayoutPassword);

        if (!FirebaseAuthManager.isUserLoggedInUsingGoogle()) {
            inputLayoutPassword.setVisibility(View.VISIBLE);
        }
    }

    private void setupDialogOnShow() {
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.error));
            button.setOnClickListener(v -> handlePositiveButtonClick());
        });
    }

    private void handlePositiveButtonClick() {
        AppCompatEditText editTextPassword = dialog.findViewById(R.id.editTextPassword);
        AppCompatEditText reasonForDelete = dialog.findViewById(R.id.reasonForDeleting);

        if (FirebaseAuthManager.isUserLoggedInUsingGoogle()) {
            deleteUserWithGoogleAuth();
            handleReasonForDelete(reasonForDelete);
        } else {
            String password = editTextPassword.getText() != null ? editTextPassword.getText().toString() : "";
            String reasonText = reasonForDelete.getText() != null ? reasonForDelete.getText().toString() : "";

            if (!password.isEmpty()) {
                deleteUserWithEmailAuth(password, reasonText);
            } else {
                ToastManager.showToast(getApplicationContext(), "Hasło nie może być puste");
            }
        }
    }

    private void handleReasonForDelete(@NonNull AppCompatEditText reasonForDelete) {
        if (reasonForDelete.getText() != null) {
            String reasonText = reasonForDelete.getText().toString();

            Runnable onSuccess = () -> dialog.dismiss();

            Runnable onFailure = () -> showToast("Error submitting feedback. Please try again.");
            submitReasonForDeleteAccountToDB(reasonText, onSuccess, onFailure);
        }
    }

    private void submitReasonForDeleteAccountToDB(@NonNull String textWithReason, @Nullable Runnable onSuccess, @Nullable Runnable onFailure) {
        if (textWithReason.isEmpty()) {
            if (onFailure != null) onFailure.run();
            return;
        }

        String time = String.valueOf(System.currentTimeMillis());
        StorageReference reasonReference = FirebaseStorage.getInstance().getReference()
                .child("DeleteAccountReasons")
                .child(time);

        byte[] data = textWithReason.getBytes(StandardCharsets.UTF_8);
        UploadTask uploadTask = reasonReference.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            if (onSuccess != null) onSuccess.run();
        }).addOnFailureListener(e -> {
            logError("Saving reason for delete to DB " + e.getMessage());
            if (onFailure != null) onFailure.run();
        });
    }

    private void showToast(String message) {
        ToastManager.showToast(getApplicationContext(), message);
    }

    private void logError(@NonNull String message) {
        Log.e("Firebase Database error ", message);
    }

    private void deleteUserWithEmailAuth(@NonNull String password, String reasonText) {
        if (currentUser != null) {
            for (UserInfo userInfo : currentUser.getProviderData()) {
                if ("password".equals(userInfo.getProviderId()) && currentUser.getEmail() != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);
                    authenticateAndDeleteUser(credential, reasonText);
                    return;
                }
            }
        }
    }

    private void deleteUserWithGoogleAuth() {
        if (currentUser == null) return;

        for (UserInfo userInfo : currentUser.getProviderData()) {
            if ("google.com".equals(userInfo.getProviderId())) {
                String idToken = getIdTokenFromSharedPreferences();

                if (!idToken.isEmpty()) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                    authenticateAndDeleteUser(credential, null);

                } else {
                    logError("idToken is null or empty");
                }
                return;
            }
        }
    }

    private void authenticateAndDeleteUser(@NonNull AuthCredential credential, @Nullable String reasonText) {
        if (currentUser != null) {
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (reasonText != null && !reasonText.isEmpty()) {
                                submitReasonForDeleteAccountToDB(reasonText, this::deleteUserAccount,
                                        () -> showToast("Nie udało się przesłać wiadomości"));
                            } else {
                                deleteUserAccount();
                            }
                        } else {
                            showToast("Błąd uwierzytelnienia");
                        }
                    })
                    .addOnFailureListener(e -> logError("Authentication error: " + e.getMessage()));
        }
    }

    private void deleteUserAccount() {
        deleteDataFromDB();
        getToastDeleteSuccessful();
        if (currentUser != null) {
            currentUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    navigateToLoginAndRegister();
                } else {
                    getToastErrorFromDeleting();
                }
            });
        }
    }

    @NonNull
    private String getIdTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sharedPreferences.getString("idToken", "");
    }

    private void navigateToLoginAndRegister() {
        Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void deleteDataFromDB() {
        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("UserModel").child(currentUser.getUid());
            userReference.removeValue();

            UserAccountLogic userAccountLogic = new UserAccountLogic();
            userAccountLogic.deleteUserAvatar();

            DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference("PostCreating");
            postsReference
                    .orderByChild("userId")
                    .equalTo(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                    postSnapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase RealmTime Database error", "Error while removing user from DB " + error.getMessage());
                        }
                    });
        }
    }

    private void getToastErrorFromDeleting() {
        ToastManager.showToast(getApplicationContext(), "Wystąpił błąd. Skontaktuj się z administratorem");
    }

    private void getToastDeleteSuccessful() {
        ToastManager.showToast(getApplicationContext(), "Pomyślnie usunięto profil");
    }
}
