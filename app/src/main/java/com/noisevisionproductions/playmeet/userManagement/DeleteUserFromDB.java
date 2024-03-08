package com.noisevisionproductions.playmeet.userManagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.loginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.postsManagement.userPosts.PostHelperSignedUpUser;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.nio.charset.StandardCharsets;

public class DeleteUserFromDB {

    @Nullable
    private FirebaseUser currentUser;
    private AlertDialog dialog;
    private final Context context;
    private final LayoutInflater layoutInflater;

    public DeleteUserFromDB(Context context, LayoutInflater layoutInflater) {
        this.context = context;
        this.layoutInflater = layoutInflater;
    }

    public void deleteUser() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        createDeleteConfirmationDialog();
    }

    private void createDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        @SuppressLint("InflateParams") View dialogView = layoutInflater.inflate(R.layout.dialog_get_password, null); // Używamy LayoutInflater dostarczonego z zewnątrz

        setupDialogView(dialogView);

        builder.setTitle(context.getString(R.string.safetyPasswordCheck))
                .setView(dialogView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        dialog = builder.create();
        setupDialogOnShow();

        if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
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
            button.setBackgroundColor(ContextCompat.getColor(context, R.color.error));
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
                ToastManager.showToast(context, "Hasło nie może być puste");
            }
        }
    }

    private void handleReasonForDelete(@NonNull AppCompatEditText reasonForDelete) {
        if (reasonForDelete.getText() != null) {
            String reasonText = reasonForDelete.getText().toString();

            Runnable onSuccess = () -> dialog.dismiss();

            submitReasonForDeleteAccountToDB(reasonText, onSuccess);
        }
    }

    private void submitReasonForDeleteAccountToDB(@NonNull String textWithReason, @Nullable Runnable onSuccess) {
        String time = String.valueOf(System.currentTimeMillis());
        StorageReference reasonReference = FirebaseStorage.getInstance().getReference()
                .child("DeleteAccountReasons")
                .child(time);

        byte[] data = textWithReason.getBytes(StandardCharsets.UTF_8);
        UploadTask uploadTask = reasonReference.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            if (onSuccess != null) onSuccess.run();
        }).addOnFailureListener(e -> logError("Saving reason for delete to DB " + e.getMessage()));
    }

    private void showToast() {
        ToastManager.showToast(context, "Błąd uwierzytelnienia");
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
                                submitReasonForDeleteAccountToDB(reasonText, this::deleteUserAccount);
                            } else {
                                deleteUserAccount();
                            }
                        } else {
                            showToast();
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
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("idToken", "");
    }

    private void navigateToLoginAndRegister() {
        Intent intent = new Intent(context, LoginAndRegisterActivity.class);
        // sprawdzam, czy dialog sie wyświetla. jeżeli tak, to go usuwam, aby zapobiec wyciekom
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    private void deleteDataFromDB() {
        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("UserModel").child(currentUser.getUid());
            userReference.removeValue();
            removeUserIdFromSavedPosts(currentUser.getUid());

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

    private void removeUserIdFromSavedPosts(String userId) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("PostCreating");
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postsSnapshot : snapshot.getChildren()) {
                    PostCreating postCreating = postsSnapshot.getValue(PostCreating.class);

                    if (postCreating != null && postCreating.getPeopleSignedUp() > 0) {
                        //   postCreating.deleteSignedUpUser(userId);
                        postCreating.setActivityFull(false);
                        PostHelperSignedUpUser.decrementJoinedPostsCount(userId);
                        postsRef.child(postCreating.getPostId()).setValue(postCreating);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", error.getMessage());
            }
        });
    }

    private void getToastErrorFromDeleting() {
        ToastManager.showToast(context, "Wystąpił błąd. Skontaktuj się z administratorem");
    }

    private void getToastDeleteSuccessful() {
        ToastManager.showToast(context, "Pomyślnie usunięto profil");
    }

}
