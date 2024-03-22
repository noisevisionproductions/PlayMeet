package com.noisevisionproductions.playmeet.utilities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.design.TopMenuLayout;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;

import java.nio.charset.StandardCharsets;

public class OpinionFromUser extends TopMenuLayout {
    private FirebaseHelper firebaseHelper;
    private TextInputEditText getText;
    private AppCompatTextView myEmail;
    private String textWithOpinion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_an_opinion);

        setUpUI();
    }

    private void setUpUI() {
        firebaseHelper = new FirebaseHelper();
        getText = findViewById(R.id.textWithOpinion);
        myEmail = findViewById(R.id.myEmail);
        myEmail.setOnClickListener(v -> copyTextOnClick(myEmail.getText().toString()));

        // ustawianie wysyłania opini za pomocą przycisku
        setUpSendOpinionButton();

        // ustawienie przycisku cofnięcia do menu głównego
        setUpBackToMainMenuButton();

        LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(this));
    }

    private void setUpSendOpinionButton() {
        CoolDownManager coolDownManager = new CoolDownManager(getApplicationContext());
        AppCompatButton sendOpinion = findViewById(R.id.sendOpinion);
        if (getText.getText() != null) {
            sendOpinion.setOnClickListener(click -> {
                textWithOpinion = getText.getText().toString();
                if (!textWithOpinion.isEmpty()) {
                    if (coolDownManager.canSendReport()) {
                        submitOpinion();
                    } else {
                        ProjectUtils.createSnackBarOnTop(this, getString(R.string.toOftenSubmitting), Color.RED);
                    }
                } else {
                    ProjectUtils.createSnackBarOnTop(this, getString(R.string.fieldCantBeEmpty), Color.RED);
                }
            });
        }
    }

    private void submitOpinion() {
        if (firebaseHelper.getCurrentUser() != null) {
            submitOpinionToFirebase(textWithOpinion);
        }
    }

    private void submitOpinionToFirebase(@NonNull String textWithOpinion) {
        String opinionId = getRefractoredString();
        StorageReference opinionReference = FirebaseStorage.getInstance()
                .getReference()
                .child("UserOpinions")
                .child(opinionId);
        byte[] data = textWithOpinion.getBytes(StandardCharsets.UTF_8);

        UploadTask uploadTask = opinionReference.putBytes(data);

        uploadTask.addOnFailureListener(e -> {
                    ProjectUtils.createSnackBarOnTop(this, getString(R.string.errorWhileSending) + " " + e, Color.RED);
                    Log.e("Firebase Database error", "Saving opinion to DB " + e.getMessage());
                })
                .addOnSuccessListener(taskSnapshot -> {
                    // czyści pole tekstowe po wysłaniu
                    clearOpinionTextField(getText);

                    // wiadomość o sukcesie wysłania
                    showOpinionSentMessage();
                });
    }

    @NonNull
    private String getRefractoredString() {
        if (firebaseHelper.getCurrentUser() != null) {

            String userId = firebaseHelper.getCurrentUser().getUid();

            String currentTime = String.valueOf(System.currentTimeMillis());

            return userId + "=" + currentTime;
        }
        return "error";
    }

    private void clearOpinionTextField(@NonNull TextInputEditText getText) {
        getText.setText("");
    }

    private void showOpinionSentMessage() {
        ProjectUtils.createSnackBarOnTop(this, getString(R.string.opinionSent), Color.GREEN);
    }

    private void copyTextOnClick(String text) {
        ProjectUtils.copyTextOnClick(getApplicationContext(), "email", text);
    }

    private void setUpBackToMainMenuButton() {
        AppCompatButton backToMainMenu = findViewById(R.id.backToMainMenu);
        ProjectUtils.backToMainMenuButton(backToMainMenu, this);
    }
}
