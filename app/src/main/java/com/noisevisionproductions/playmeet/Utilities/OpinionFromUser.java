package com.noisevisionproductions.playmeet.Utilities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.noisevisionproductions.playmeet.Design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class OpinionFromUser extends SidePanelBaseActivity {
    private FirebaseHelper firebaseHelper;
    private TextInputEditText getText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_an_opinion);

        // zaladowanie panelu bocznego
        setupDrawerLayout();
        setupNavigationView();

        setUpUI();
    }

    private void setUpUI() {
        View view = getWindow().getDecorView().getRootView();
        firebaseHelper = new FirebaseHelper();
        getText = findViewById(R.id.textWithOpinion);

        // ustawianie wysyłania opini za pomocą przycisku
        setUpSendOpinionButton(view);

        // ustawienie przycisku cofnięcia do menu głównego
        setUpBackToMainMenuButton();

        LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> NavigationUtils.hideSoftKeyboard(this));
    }

    private void setUpSendOpinionButton(View view) {
        AppCompatButton sendOpinion = findViewById(R.id.sendOpinion);
        sendOpinion.setOnClickListener(click -> submitOpinion(view));
    }

    private void setUpBackToMainMenuButton() {
        AppCompatButton backToMainMenu = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(backToMainMenu, this);
    }

    private void submitOpinion(View view) {
        if (getText.getText() != null) {
            String textWithOpinion = getText.getText().toString();

            if (!textWithOpinion.isEmpty()) {
                if (firebaseHelper.getCurrentUser() != null) {
                    String currentUserId = firebaseHelper.getCurrentUser().getUid();

                    handleOpinionSubmission(currentUserId, textWithOpinion, view);
                }
            } else {
                Snackbar.make(view, "Pole nie może być puste!", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void handleOpinionSubmission(String currentUserId, String textWithOpinion, View view) {
        getOpinionCountForUser(currentUserId, opinionCount -> {
            // sprawdzanie, czy limit nie został przekroczony
            if (opinionCount < 3) {
                submitOpinionToFirebase(currentUserId, textWithOpinion, view);
            } else {
                showOpinionLimitReachedMessage(view);
            }
        });
    }

    private void submitOpinionToFirebase(String currentUserId, String textWithOpinion, View view) {
        String opinionId = getRefractoredString();
        StorageReference opinionReference = FirebaseStorage.getInstance().getReference().child("UserOpinions").child(opinionId);
        byte[] data = textWithOpinion.getBytes(StandardCharsets.UTF_8);

        UploadTask uploadTask = opinionReference.putBytes(data);

        uploadTask.addOnFailureListener(e -> Snackbar.make(view, "Wystąpił błąd podczas wysyłania " + e, Snackbar.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> {
                    // czyści pole tekstowe po wysłaniu
                    clearOpinionTextField(getText);

                    // wiadomość o sukcesie wysłania
                    showOpinionSentMessage(view);

                    incrementOpinionCountForUser(currentUserId);
                });
    }

    private String getRefractoredString() {
        String userId = firebaseHelper.getCurrentUser().getUid();

        String currentTime = String.valueOf(System.currentTimeMillis());

        return userId + "=" + currentTime;
    }

    private void clearOpinionTextField(TextInputEditText getText) {
        getText.setText("");
    }

    private void showOpinionSentMessage(View view) {
        Snackbar.make(view, "Opinia wysłana!", Snackbar.LENGTH_LONG)
                .setAction("Menu Główne", v -> navigateToMainMenu(view))
                .show();
    }

    private void navigateToMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainMenuPosts.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        view.getContext().startActivity(intent);
    }

    private void showOpinionLimitReachedMessage(View view) {
        Snackbar.make(view, "Osiągnąłeś limit wysyłania opinii.", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
    }

    public void incrementOpinionCountForUser(String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserOpinionsCount").child(userId);

        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer count = mutableData.getValue(Integer.class);
                if (count == null) {
                    // Jeśli to jest pierwsza opinia tego użytkownika, ustaw liczbę opinii na 1
                    mutableData.setValue(1);
                } else {
                    // W przeciwnym razie zwiększ liczbę opinii o 1
                    mutableData.setValue(count + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
            }
        });
    }

    private void getOpinionCountForUser(String userId, OnOpinionCountCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserOpinionsCount").child(userId);

        ref.get().addOnSuccessListener(dataSnapshot -> {
            Integer count = dataSnapshot.getValue(Integer.class);
            // Jeśli nie ma jeszcze żadnych opinii dla tego użytkownika, ustaw liczbę opinii na 0
            int opinionCount = Objects.requireNonNullElse(count, 0);
            callback.onOpinionCountReceived(opinionCount);
        }).addOnFailureListener(e -> {
            // Obsłuż błąd
            callback.onOpinionCountReceived(0);
        });
    }

    private interface OnOpinionCountCallback {
        void onOpinionCountReceived(int opinionCount);
    }
}
