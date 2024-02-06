package com.noisevisionproductions.playmeet.utilities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.noisevisionproductions.playmeet.design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.postsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;

import java.nio.charset.StandardCharsets;

public class OpinionFromUser extends SidePanelBaseActivity {
    private FirebaseHelper firebaseHelper;
    private TextInputEditText getText;
    private AppCompatTextView myEmail;
    private String textWithOpinion;

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
        myEmail = findViewById(R.id.myEmail);
        AppCompatImageButton copyButton = findViewById(R.id.copyButton);
        copyButton.setOnClickListener(v -> copyTextOnClick(myEmail.getText().toString()));

        // ustawianie wysyłania opini za pomocą przycisku
        setUpSendOpinionButton(view);

        // ustawienie przycisku cofnięcia do menu głównego
        setUpBackToMainMenuButton();

        LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(this));
    }

    private void setUpSendOpinionButton(@NonNull View view) {
        CoolDownManager coolDownManager = new CoolDownManager(getApplicationContext());
        AppCompatButton sendOpinion = findViewById(R.id.sendOpinion);
        if (getText.getText() != null) {

            textWithOpinion = getText.getText().toString();

            sendOpinion.setOnClickListener(click -> {
                if (!textWithOpinion.isEmpty()) {
                    if (coolDownManager.canSendReport()) {
                        submitOpinion(view);
                    } else {
                        Snackbar.make(view, "Zbyt częste zgłaszanie", Snackbar.LENGTH_SHORT)
                                .setTextColor(Color.RED).show();
                    }
                } else {
                    Snackbar.make(view, "Pole nie może być puste!", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void submitOpinion(@NonNull View view) {
        if (firebaseHelper.getCurrentUser() != null) {
            submitOpinionToFirebase(textWithOpinion, view);
        }
    }

    private void submitOpinionToFirebase(@NonNull String textWithOpinion, @NonNull View view) {
        String opinionId = getRefractoredString();
        StorageReference opinionReference = FirebaseStorage.getInstance().getReference().child("UserOpinions").child(opinionId);
        byte[] data = textWithOpinion.getBytes(StandardCharsets.UTF_8);

        UploadTask uploadTask = opinionReference.putBytes(data);

        uploadTask.addOnFailureListener(e -> {
                    Snackbar.make(view, "Wystąpił błąd podczas wysyłania " + e, Snackbar.LENGTH_SHORT).show();
                    Log.e("Firebase Database error", "Saving opinion to DB " + e.getMessage());
                })
                .addOnSuccessListener(taskSnapshot -> {
                    // czyści pole tekstowe po wysłaniu
                    clearOpinionTextField(getText);

                    // wiadomość o sukcesie wysłania
                    showOpinionSentMessage(view);
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

    private void showOpinionSentMessage(@NonNull View view) {
        Snackbar.make(view, "Opinia wysłana!", Snackbar.LENGTH_LONG)
                .setAction("Menu Główne", v -> navigateToMainMenu(view))
                .show();
    }

    private void navigateToMainMenu(@NonNull View view) {
        Intent intent = new Intent(getApplicationContext(), MainMenuPosts.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        view.getContext().startActivity(intent);
    }

    private void copyTextOnClick(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("email", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    private void setUpBackToMainMenuButton() {
        AppCompatButton backToMainMenu = findViewById(R.id.backToMainMenu);
        ProjectUtils.backToMainMenuButton(backToMainMenu, this);
    }
}
